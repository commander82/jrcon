#!/bin/bash

set -e

# Variablen
PKG_NAME="jrcon"
VERSION="1.0"
MAINTAINER="Commander82 <viktorskalin@yahoo.de>"
GITHUB_URL="https://github.com/commander82/jrcon"
BUILD_DIR="${PKG_NAME}-${VERSION}"

echo "📁 Erstelle Verzeichnisstruktur für Debian-Paket: $BUILD_DIR"

# Verzeichnisstruktur anlegen
mkdir -p "${BUILD_DIR}/debian"
mkdir -p "${BUILD_DIR}/usr/bin"
mkdir -p "${BUILD_DIR}/usr/share/${PKG_NAME}"

# Beispieldateien kopieren
cp jrcon.jar "${BUILD_DIR}/usr/share/${PKG_NAME}/jrcon.jar"

# Wrapper-Skript erstellen
cat << 'EOF' > "${BUILD_DIR}/usr/bin/jcon"
#!/bin/sh
exec java -jar /usr/share/jrcon/jrcon.jar "$@"
EOF
chmod +x "${BUILD_DIR}/usr/bin/jcon"

# Debian-Kontroll-Dateien erstellen
cat << EOF > "${BUILD_DIR}/debian/control"
Source: ${PKG_NAME}
Section: utils
Priority: optional
Maintainer: ${MAINTAINER}
Standards-Version: 4.5.0
Package: ${PKG_NAME}
Architecture: all
Depends: default-jre
Description: JRcon - Java RCON-Client für Server-Fernsteuerung
 Eine einfache Open-Source-Anwendung zum Fernsteuern von Servern via RCON.
EOF

cat << EOF > "${BUILD_DIR}/debian/changelog"
${PKG_NAME} (${VERSION}) unstable; urgency=low

  * Initial release

 -- ${MAINTAINER}  $(date -R)
EOF

cat << EOF > "${BUILD_DIR}/debian/compat"
11
EOF

cat << EOF > "${BUILD_DIR}/debian/rules"
#!/usr/bin/make -f
%:
	dh \$@
EOF
chmod +x "${BUILD_DIR}/debian/rules"

cat << EOF > "${BUILD_DIR}/debian/copyright"
Format: https://www.debian.org/doc/packaging-manuals/copyright-format/1.0/
Upstream-Name: JRcon
Source: ${GITHUB_URL}

Files: *
Copyright: 2025 ${MAINTAINER}
License: Custom

 JRcon Lizenz (Benutzerdefiniert)

 Diese Software darf kostenlos genutzt, verändert und verteilt werden,
 solange sie nur nicht-kommerziell verwendet und nur über GitHub verbreitet wird.
 Für Details siehe LICENSE.txt.
EOF

echo "✅ Struktur erfolgreich erstellt: ${BUILD_DIR}"
echo "👉 Du kannst jetzt mit dem Bauen beginnen: cd ${BUILD_DIR} && dpkg-buildpackage -us -uc"
