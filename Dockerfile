# Dockerfile para InventarioWebPRN335
# JakartaEE 10.0 + OpenLiberty 25.0.0.8 + JasperReports + PostgreSQL

FROM debian:12

# Variables de entorno
ENV JAVA_HOME=/opt/jdk-21
ENV LIBERTY_HOME=/opt/wlp
ENV PATH=$JAVA_HOME/bin:$LIBERTY_HOME/bin:$PATH
ENV JAVA_OPTS="-Djava.awt.headless=true -Dprism.order=sw"
ENV LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu:$LD_LIBRARY_PATH

# Instalar dependencias para JasperReports
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    fontconfig \
    libfreetype6 \
    libfreetype6-dev \
    fonts-dejavu \
    fonts-dejavu-core \
    fonts-dejavu-extra \
    fonts-liberation \
    fonts-liberation2 \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Actualizar cache de fuentes
RUN fc-cache -fv

# Descargar e instalar JDK 21
RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz -O /tmp/jdk-21.tar.gz && \
    mkdir -p /opt && \
    tar -xzf /tmp/jdk-21.tar.gz -C /opt && \
    mv /opt/jdk-21* /opt/jdk-21 && \
    rm /tmp/jdk-21.tar.gz

# Crear symlinks necesarios para libfreetype (después de instalar JDK)
RUN ln -sf /usr/lib/x86_64-linux-gnu/libfreetype.so.6 /usr/lib/libfreetype.so.6 && \
    mkdir -p /opt/jdk-21/lib && \
    ln -sf /usr/lib/x86_64-linux-gnu/libfreetype.so.6 /opt/jdk-21/lib/libfreetype.so.6 && \
    ldconfig

# Descargar e instalar OpenLiberty 25.0.0.8
RUN wget https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/release/25.0.0.8/openliberty-jakartaee10-25.0.0.8.zip -O /tmp/openliberty.zip && \
    unzip /tmp/openliberty.zip -d /opt && \
    rm /tmp/openliberty.zip

# Crear directorio del servidor
RUN mkdir -p /opt/wlp/usr/servers/defaultServer/apps && \
    mkdir -p /opt/wlp/usr/servers/defaultServer/dropins && \
    mkdir -p /opt/wlp/usr/servers/defaultServer/resources/security && \
    mkdir -p /opt/wlp/lib

# Descargar driver PostgreSQL (no requiere tenerlo en el proyecto)
RUN wget https://jdbc.postgresql.org/download/postgresql-42.7.4.jar -O /opt/wlp/lib/postgresql-42.7.4.jar

# Copiar WAR de la aplicación
COPY target/InventarioWebapprn335-1.0-SNAPSHOT.war /opt/wlp/usr/servers/defaultServer/apps/inventario.war

# Copiar configuración del servidor
COPY server.xml /opt/wlp/usr/servers/defaultServer/server.xml

# Copiar script de inicialización
COPY init-server.sh /opt/init-server.sh
RUN chmod +x /opt/init-server.sh

# Exponer puerto
EXPOSE 9080 9443

# Directorio de trabajo
WORKDIR /opt/wlp/usr/servers/defaultServer

# Comando de inicio usando el script
CMD ["/opt/init-server.sh"]
