package de.jrcon;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 4) {
            // Direkt-Modus
            handleDirect(args);
        } else {
            // Interaktiv
            handleInteractive();
        }
    }

    private static void handleDirect(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String password = args[2];
        String command = args[3];

        try {
            RconClient client = new RconClient(host, port, password);
            String response = client.sendCommand(command);
            System.out.println(response);
            client.close();
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }

    private static void handleInteractive() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Host: ");
            String host = reader.readLine();

            System.out.print("Port: ");
            int port = Integer.parseInt(reader.readLine());

            System.out.print("Passwort: ");
            String password = reader.readLine();

            RconClient client = new RconClient(host, port, password);
            System.out.println("✅ Eingeloggt. Gib `quit()` zum Beenden ein.");

            while (true) {
                System.out.print("Cmd: ");
                String cmd = reader.readLine();
                if ("quit()".equalsIgnoreCase(cmd.trim())) {
                    break;
                }

                String response = client.sendCommand(cmd);
                System.out.println(response);
            }

            client.close();

        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }
}
