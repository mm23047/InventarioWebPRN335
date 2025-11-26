#!/bin/bash
# Script de inicialización para OpenLiberty

# Crear directorio de seguridad si no existe
mkdir -p /opt/wlp/usr/servers/defaultServer/resources/security

# Generar keyStore si no existe
if [ ! -f "/opt/wlp/usr/servers/defaultServer/resources/security/key.p12" ]; then
    echo "Generando keyStore..."
    /opt/jdk-21/bin/keytool -genkeypair \
        -alias default \
        -keyalg RSA \
        -keysize 2048 \
        -validity 3650 \
        -keystore /opt/wlp/usr/servers/defaultServer/resources/security/key.p12 \
        -storepass Liberty \
        -keypass Liberty \
        -storetype PKCS12 \
        -dname "CN=localhost, OU=Development, O=UES, L=San Salvador, ST=San Salvador, C=SV" \
        -noprompt
    echo "KeyStore generado exitosamente"
else
    echo "KeyStore ya existe"
fi

# Configurar variables de entorno para JasperReports
export JAVA_OPTS="-Djava.awt.headless=true -Dnet.sf.jasperreports.awt.ignore.missing.font=true"
export JVM_ARGS="-Djava.awt.headless=true -Dnet.sf.jasperreports.awt.ignore.missing.font=true"

echo "Iniciando OpenLiberty con JAVA_OPTS: $JAVA_OPTS"

# Iniciar OpenLiberty
exec /opt/wlp/bin/server run defaultServer


