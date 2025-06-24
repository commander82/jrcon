FROM debian:bookworm

RUN apt update && apt install -y \
    build-essential \
    devscripts \
    debhelper \
    fakeroot \
    default-jre

# optional: Arbeitsverzeichnis setzen
WORKDIR /build
