package de.jrcon;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class RconClient {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private int requestId = 0;

    public RconClient(String host, int port, String password) throws IOException {
        socket = new Socket(host, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        // Authentifizieren
        if (!authenticate(password)) {
            throw new IOException("Authentifizierung fehlgeschlagen!");
        }
    }

    private boolean authenticate(String password) throws IOException {
        requestId = 1;
        sendPacket(requestId, 3, password); // Type 3 = AUTH
        RconResponse response = readResponse();

        return response.getRequestId() == requestId;
    }

    public String sendCommand(String command) throws IOException {
        requestId++;
        sendPacket(requestId, 2, command); // Type 2 = EXEC_COMMAND
        RconResponse response = readResponse();
        return response.getBody();
    }

    private void sendPacket(int id, int type, String body) throws IOException {
        byte[] bodyBytes = body.getBytes("UTF-8");
        int size = 4 + 4 + bodyBytes.length + 2;

        ByteBuffer buffer = ByteBuffer.allocate(4 + size);
        buffer.putInt(size);
        buffer.putInt(id);
        buffer.putInt(type);
        buffer.put(bodyBytes);
        buffer.put((byte) 0); // Null-Terminator 1
        buffer.put((byte) 0); // Null-Terminator 2

        out.write(buffer.array());
    }

    private RconResponse readResponse() throws IOException {
        int length = in.readInt();
        int id = in.readInt();
        int type = in.readInt();

        byte[] payload = new byte[length - 8];
        in.readFully(payload);

        // String endet vor dem ersten Nullbyte
        int endIndex = 0;
        while (endIndex < payload.length && payload[endIndex] != 0) endIndex++;
        String body = new String(payload, 0, endIndex, "UTF-8");

        return new RconResponse(id, type, body);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    // Hilfsklasse für Antwort
    private static class RconResponse {
        private int requestId;
        private int type;
        private String body;

        public RconResponse(int requestId, int type, String body) {
            this.requestId = requestId;
            this.type = type;
            this.body = body;
        }

        public int getRequestId() { return requestId; }
        public String getBody() { return body; }
    }
}
