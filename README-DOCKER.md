# Dockerización de InventarioWebPRN335

## Inicio Rápido - Configuración Desde Cero

**Si acabas de clonar el repositorio** y es la primera vez que lo configuras, sigue estos pasos en orden:

### Pre-requisitos
Asegúrate de tener instalado:
- [Docker Desktop](https://www.docker.com/products/docker-desktop) corriendo
- [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- VS Code o IntelliJ IDEA

### Pasos de Configuración (Ejecútalo en este orden)

```powershell
# PASO 1: Navegar al directorio del proyecto
cd c:\Users\TU_USUARIO\Desktop\InventarioWebPRN335

# PASO 2: Compilar la aplicación con Maven
mvn clean package

# PASO 3: Verificar que se creó el WAR
ls target\InventarioWebapprn335-1.0-SNAPSHOT.war

# PASO 4: Crear la red Docker
docker network create inventario-network

# PASO 5: Verificar que tienes PostgreSQL corriendo
docker ps --filter "name=db17"

# PASO 6: Verificar que db17 está corriendo
# IMPORTANTE: db17 debe existir previamente y estar corriendo
# Si db17 NO está corriendo, inícialo:
docker start db17

# PASO 7: Conectar db17 a la red inventario-network
# (Ignora el error si ya está conectado)
docker network connect inventario-network db17

# PASO 8: Construir la imagen Docker
docker-compose build inventario-app

# PASO 9: Levantar la aplicación
docker-compose up -d inventario-app

# PASO 10: Verificar que db17 esté en la red inventario-network
docker network inspect inventario-network --format '{{range .Containers}}{{.Name}} {{end}}'
# Deberías ver: db17 inventario-web

# PASO 11: Crear la vista Kardex_Implementado en PostgreSQL
# Esta vista es necesaria para que funcionen los reportes Kardex
docker exec -i db17 psql -U postgres -d inventario_prn335 < create_kardex_view.sql

# PASO 11: IMPORTANTE - Crear la vista Kardex_Implementado en PostgreSQL
# Esta vista es NECESARIA para que funcionen los reportes de Kardex
# Si no ejecutas esto, los PDFs saldrán en blanco
Get-Content create_kardex_view.sql | docker exec -i db17 psql -U postgres -d inventario_prn335

# PASO 12: Ver los logs para confirmar que inició correctamente
docker logs -f inventario-web
# Presiona Ctrl+C para salir de los logs
# Busca el mensaje: "The defaultServer server is ready to run a smarter planet"

# PASO 13: Probar la API REST
curl http://localhost:9080/resources/v1/tipo_almacen

# PASO 14: Abrir en el navegador
# Aplicación web JSF:
# http://localhost:9080/Paginas/TipoAlmacen.jsf
# API REST:
# http://localhost:9080/resources/v1/tipo_almacen
```

### Verificación Final

Ejecuta estos comandos para verificar que todo está corriendo:

```powershell
# Ver contenedores corriendo (deberías ver inventario-web y db17)
docker ps

# Probar acceso a la aplicación
Start-Process "http://localhost:9080"

# Verificar librerías de fuentes (debería mostrar ~50)
docker exec inventario-web bash -c "fc-list | wc -l"
```

**¡Listo!** Tu aplicación debería estar corriendo en http://localhost:9080

---

## 🔄 Inicio Rápido - Si Ya Está Configurado

Si ya configuraste todo anteriormente y solo necesitas levantar la aplicación:

```powershell
# Navegar al proyecto
cd C:\Users\melya\Desktop\e\InventarioWebPRN335

# Levantar la aplicación
docker-compose up -d inventario-app

# IMPORTANTE: Si los reportes de Kardex salen en blanco, ejecuta esto:
Get-Content create_kardex_view.sql | docker exec -i db17 psql -U postgres -d inventario_prn335

# Ver logs
docker logs -f inventario-web
```

---

## Solución de Problemas

### PDFs de Kardex salen en blanco

**Causa**: La vista `Kardex_Implementado` no está creada en la base de datos.

**Solución**:
```powershell
Get-Content create_kardex_view.sql | docker exec -i db17 psql -U postgres -d inventario_prn335
```

**Verificar que la vista existe**:
```powershell
docker exec -i db17 psql -U postgres -d inventario_prn335 -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'VIEW';"
```

Deberías ver `Kardex_Implementado` en la lista.

---

## Requisitos Previos
- Docker Desktop instalado y corriendo
- Docker Compose incluido con Docker Desktop
- Java JDK 21 y Maven instalados (para compilar)
- PostgreSQL contenedor `db17` corriendo (o crear uno nuevo)

**Nota**: El driver PostgreSQL se descarga automáticamente durante la construcción de la imagen Docker, no necesitas descargarlo manualmente.

## Estructura de Archivos Docker

```
InventarioWebPRN335/
├── Dockerfile                      # Imagen de la aplicación
├── docker-compose.yml              # Orquestación de servicios
├── server.xml                      # Configuración OpenLiberty
├── init-server.sh                  # Script de inicio del servidor
├── .dockerignore                   # Archivos excluidos del build
├── .gitignore                      # Archivos excluidos de Git
├── pom.xml                         # Configuración Maven
└── target/
    └── InventarioWebapprn335-1.0-SNAPSHOT.war  # WAR compilado
```

**Nota**: El driver PostgreSQL (`postgresql-42.7.4.jar`) ya NO es necesario en el proyecto. Se descarga automáticamente durante el build de Docker.

## Paso 1: Compilar la Aplicación

```bash
mvn clean package
```

**Verifica que se compiló correctamente:**
```powershell
# Windows PowerShell
ls target\InventarioWebapprn335-1.0-SNAPSHOT.war

# Linux/Mac
ls -lh target/InventarioWebapprn335-1.0-SNAPSHOT.war
```

Deberías ver un archivo WAR de aproximadamente **20-30 MB**.

## Paso 2: Construir la Imagen Docker

### Opción A: Con Docker Compose (Recomendado)
```bash
docker-compose build inventario-app
```

### Opción B: Con Docker directamente
```bash
# Construcción normal (usa caché)
docker build -t inventario-web:latest .

# Construcción completa (sin caché - usa esto si hay problemas)
docker build --no-cache -t inventario-web:latest .
```

**Nota**: La construcción tarda **5-10 minutos** la primera vez porque descarga:
- JDK 21 (~170 MB)
- OpenLiberty (~45 MB)
- Driver PostgreSQL (~1 MB) - **Se descarga automáticamente**

## Paso 3: Ejecutar la Aplicación

### Usar Base de Datos Existente (db17)

**IMPORTANTE:** Este proyecto usa un contenedor PostgreSQL existente llamado `db17` que debe estar corriendo previamente.

```powershell
# 1. Verificar que db17 esté corriendo
docker ps --filter "name=db17"

# 2. Si db17 está detenido, inícialo
docker start db17

# 3. Crear la red inventario-network (si no existe)
docker network create inventario-network

# 4. Conectar db17 a la red (ignora el error si ya está conectado)
docker network connect inventario-network db17

# 5. Levantar la aplicación inventario
docker-compose up -d inventario-app
```

### Verificar que Levantó Correctamente

```powershell
# Ver contenedores corriendo
docker ps

# Deberías ver:
# - inventario-web (puertos 9080, 9443)
# - db17 (puerto 5432)
```

## Paso 4: Verificar el Despliegue

### Ver logs en tiempo real
```bash
docker logs -f inventario-web
```

### ¿Qué mensajes son normales?

**ÉXITO** - Busca este mensaje:
```
[AUDIT] CWWKZ0001I: Application inventario started in XX.XXX seconds.
[AUDIT] CWWKF0011I: The defaultServer server is ready to run a smarter planet.
```

**WARNINGS ESPERADOS** (puedes ignorarlos):
- `CWWKS9582E` sobre SSL/ORB - El keyStore se genera automáticamente, es normal
- `CNTR4016W` sobre JmsQueue - Normal si no usas mensajería JMS
- `MyFaces Core is running in DEVELOPMENT mode` - Esperado en desarrollo
- `CWWKZ0022W: Application inventario has not started in 30 seconds` - Solo informativo, espera unos segundos más

**ERRORES REALES** (necesitan corrección):
- `Could not initialize class sun.awt.X11FontManager` - Ver sección Troubleshooting
- `Connection refused` a la base de datos - Verifica que db17 esté corriendo

### Ver logs de la base de datos
```bash
docker logs -f db17
```

### Verificar que la aplicación esté corriendo
```bash
docker ps
```

Deberías ver:
- `inventario-web` corriendo en puertos 9080, 9443
- `db17` corriendo en puerto 5432

### 🗄️ Configurar la Vista Kardex_Implementado

**IMPORTANTE:** Antes de generar reportes Kardex, necesitas crear una vista en PostgreSQL.

```powershell
# Ejecutar el script SQL para crear la vista
docker exec -i db17 psql -U postgres -d inventario_prn335 < create_kardex_view.sql

# Verificar que se creó correctamente
docker exec -i db17 psql -U postgres -d inventario_prn335 -c '\dv "Kardex_Implementado"'
```

**Resultado esperado:** Deberías ver la definición de la vista `"Kardex_Implementado"`.

**¿Qué hace esta vista?**
- Une las tablas `kardex` y `producto`
- Calcula entradas, salidas, saldo y valores totales
- Es necesaria para que los reportes PDF funcionen correctamente

## 🌐 Acceder a la Aplicación

Una vez que veas el mensaje de éxito en los logs:

### URLs de Acceso
- 🌍 **HTTP**: http://localhost:9080
- 🔒 **HTTPS**: https://localhost:9443
- 📡 **API REST Base**: http://localhost:9080/resources/v1

### Probar la API REST

```powershell
# Listar todos los tipos de almacén
curl http://localhost:9080/resources/v1/tipo_almacen

# Obtener un tipo de almacén específico (ID=1)
curl http://localhost:9080/resources/v1/tipo_almacen/1
```

**Endpoints REST Disponibles:**
- `/resources/v1/tipo_almacen` - Tipos de almacén
- `/resources/v1/tipo_producto` - Tipos de producto
- `/resources/v1/tipo_unidad_medida` - Tipos de unidad de medida
- `/resources/v1/producto` - Productos
- `/resources/v1/cliente` - Clientes
- `/resources/v1/proveedor` - Proveedores

### Probar Páginas JSF Web

1. **Tipo de Almacén**: http://localhost:9080/Paginas/TipoAlmacen.jsf
2. **Tipo de Producto**: http://localhost:9080/Paginas/TipoProducto.jsf
3. **Productos**: http://localhost:9080/Paginas/Producto.jsf
4. **Clientes**: http://localhost:9080/Paginas/Cliente.jsf

### Probar Funcionalidad Básica
1. Abre http://localhost:9080/Paginas/TipoAlmacen.jsf en tu navegador
2. Deberías ver la lista de tipos de almacén con opciones para crear, editar y eliminar

### Probar Reportes Kardex (JasperReports)
¡Esto es lo que corregimos! Ahora debería funcionar sin errores:

1. Navega a: http://localhost:9080/Paginas/Producto.xhtml
2. Selecciona un producto de la lista
3. Haz clic en **"Reportes"** o el botón de reporte
4. Selecciona el rango de fechas
5. Haz clic en **"Generar Reporte PDF"**
6. El PDF debería descargarse automáticamente

**Si funciona**: ¡Perfecto! Las librerías de fuentes están correctamente instaladas.
**Si falla**: Ver sección de Troubleshooting abajo.

## 🔄 Detener y Reiniciar

### Detener la aplicación (mantiene los datos)
```bash
docker-compose down
```

### Reiniciar la aplicación
```bash
docker-compose up -d inventario-app
```

### Ver estado de contenedores
```bash
# Contenedores corriendo
docker ps

# Todos los contenedores (incluyendo detenidos)
docker ps -a
```

## Reconstruir la Imagen

### Error: "Could not initialize class sun.awt.X11FontManager"

**Síntoma**: Al generar reportes PDF con JasperReports, sale este error.

**Causa**: Faltan las librerías de fuentes (`libfreetype`) o los symlinks no están configurados.

**Solución**:

1. **Reconstruir la imagen completamente**:
```powershell
docker-compose down
docker-compose build --no-cache inventario-app
docker-compose up -d inventario-app
```

2. **Verificar que las librerías estén instaladas**:
```bash
# Verificar libfreetype
docker exec inventario-web ls -lh /usr/lib/x86_64-linux-gnu/libfreetype.so.6

# Verificar symlinks
docker exec inventario-web ls -lh /usr/lib/libfreetype.so.6
docker exec inventario-web ls -lh /opt/jdk-21/lib/libfreetype.so.6

# Verificar fuentes instaladas (debería mostrar ~50 fuentes)
docker exec inventario-web bash -c "fc-list | wc -l"
```

3. **Verificar que el WAR contenga los .jasper**:
```bash
docker exec inventario-web unzip -l /opt/wlp/usr/servers/defaultServer/apps/inventario.war | grep jasper
```

Deberías ver:
```
reports/kardex.jasper
reports/tipo_unidad_medida.jasper
```

---

### Error: "Cannot connect to database"

**Síntoma**: La aplicación no puede conectarse a PostgreSQL.

**Soluciones**:

1. **Verificar que db17 esté corriendo**:
```bash
docker ps --filter "name=db17"
```

2. **Verificar que db17 esté en la misma red**:
```bash
docker network inspect inventario-network
```

Deberías ver `db17` en la lista de contenedores.

3. **Conectar db17 manualmente**:
```bash
docker network connect inventario-network db17
```

4. **Probar conexión directa a la BD**:
```bash
docker exec -it db17 psql -U postgres -d inventario_prn335 -c "\dt"
```

---

### Error: "Address already in use" (Puerto en uso)

**Síntoma**: No puede levantar el contenedor porque el puerto 9080 o 5432 ya está en uso.

**Solución 1** - Detener el proceso que usa el puerto:
```powershell
# Ver qué proceso usa el puerto 9080
netstat -ano | findstr :9080

# Matar el proceso (reemplaza PID con el número que viste)
Stop-Process -Id PID -Force
```

**Solución 2** - Cambiar el puerto en `docker-compose.yml`:
```yaml
ports:
  - "8080:9080"  # Usar puerto 8080 en lugar de 9080
  - "8443:9443"  # Usar puerto 8443 en lugar de 9443
```

---

### Error: "postgresql-42.7.4.jar not found"

**Síntoma**: Al construir la imagen, falla porque no encuentra el driver JDBC.

**Causa**: El Dockerfile ya descarga automáticamente el driver PostgreSQL. Este error no debería aparecer.

**Solución**: Si aparece este error, verifica que tienes conexión a internet durante el build de Docker. El driver se descarga automáticamente con `wget` en el Dockerfile.

---

### Ver Logs Completos del Servidor

Si necesitas ver logs más detallados:

```bash
# Entrar al contenedor
docker exec -it inventario-web bash

# Ver logs de OpenLiberty
cat /opt/wlp/usr/servers/defaultServer/logs/messages.log
cat /opt/wlp/usr/servers/defaultServer/logs/console.log

# Salir del contenedor
exit
```

## 🔄 Hot Reload Durante Desarrollo

**¿Qué es?** Permite recompilar y ver cambios sin reconstruir la imagen Docker.

### Cómo Activar

1. **Descomentar el volumen en `docker-compose.yml`**:
```yaml
services:
  inventario-app:
    volumes:
      - ./target/InventarioWebapprn335-1.0-SNAPSHOT.war:/opt/wlp/usr/servers/defaultServer/apps/inventario.war
```

2. **Flujo de trabajo**:
```bash
# 1. Hacer cambios en el código Java
# 2. Recompilar
mvn clean package

# 3. OpenLiberty detecta el cambio automáticamente y recarga
# 4. Espera 5-10 segundos
# 5. Refresca el navegador
```

3. **Ver la recarga en los logs**:
```bash
docker logs -f inventario-web

# Busca:
# [AUDIT] CWWKT0017I: Web application removed (default_host): http://...
# [AUDIT] CWWKZ0009I: The application inventario has stopped successfully.
# [AUDIT] CWWKZ0018I: Starting application inventario.
# [AUDIT] CWWKZ0001I: Application inventario started in X.XXX seconds.
```

**Nota**: Solo funciona para cambios en código Java. Para cambios en `Dockerfile` o `server.xml`, debes reconstruir.

## Reconstruir la Imagen

### ¿Cuándo reconstruir?

Reconstruye la imagen cuando modifiques:
- `Dockerfile`
- `server.xml`
- `init-server.sh`
- Dependencias del `pom.xml` (librerías)
- Archivos de configuración (`.properties`, `persistence.xml`)

**NO** necesitas reconstruir para cambios en:
- Código Java (.java files) - usa Hot Reload
- Páginas XHTML - usa Hot Reload

### Comandos de Reconstrucción

```bash
# Reconstrucción rápida (usa caché)
docker-compose down
docker-compose build inventario-app
docker-compose up -d inventario-app
```

```bash
# Reconstrucción completa (sin caché - usa si hay problemas)
docker-compose down
docker-compose build --no-cache inventario-app
docker-compose up -d inventario-app
```

```bash
# Ver el progreso de la construcción
docker-compose build --progress=plain inventario-app
```

## Comandos Utiles

### Gestión de Contenedores

```bash
# Ver contenedores corriendo
docker ps

# Ver todos los contenedores (incluidos detenidos)
docker ps -a

# Ver logs en tiempo real
docker logs -f inventario-web

# Ver últimas 50 líneas de logs
docker logs --tail 50 inventario-web

# Entrar al contenedor (bash interactivo)
docker exec -it inventario-web bash

# Reiniciar solo la aplicación
docker restart inventario-web

# Detener la aplicación
docker stop inventario-web

# Eliminar el contenedor
docker rm inventario-web
```

### Gestión de Imágenes

```bash
# Ver imágenes
docker images

# Eliminar imagen
docker rmi inventariowebprn335-inventario-app:latest

# Limpiar imágenes sin usar
docker image prune -a
```

### Gestión de Redes

```bash
# Ver redes
docker network ls

# Inspeccionar red
docker network inspect inventario-network

# Ver qué contenedores están en la red
docker network inspect inventario-network | findstr Name

# Desconectar contenedor de la red
docker network disconnect inventario-network db17
```

### Limpieza General

```bash
# Limpiar todo (contenedores detenidos, redes sin usar, imágenes sin usar)
docker system prune -a

# Limpiar volúmenes sin usar (¡CUIDADO! Pierdes datos)
docker volume prune

# Ver espacio usado por Docker
docker system df
```

---

## Stack Tecnológico del Contenedor

| Componente | Versión | Descripción |
|------------|---------|-------------|
| **Sistema Operativo** | Debian 12 (Bookworm) | Base ligera y estable |
| **Java** | Oracle JDK 21 | Última versión LTS de Java |
| **Servidor de Aplicaciones** | OpenLiberty 25.0.0.8 | Compatible con Jakarta EE 10.0 |
| **Base de Datos** | PostgreSQL 17 | Última versión de PostgreSQL |
| **Driver JDBC** | postgresql-42.7.4.jar | Driver oficial de PostgreSQL |
| **Framework Web** | PrimeFaces 15.0.8 | UI components para JSF |
| **Reportes** | JasperReports 7.0.3 | Generación de PDF |
| **Fuentes** | DejaVu, Liberation | Fuentes para reportes PDF |

## Características Configuradas

### Jakarta EE 10.0 Features Instaladas

Jakarta EE 10.0 Core  
Jakarta Faces 4.0 (JSF)  
Jakarta RESTful Web Services 3.1  
Jakarta Persistence 3.1 (JPA)  
Jakarta Enterprise Beans 4.0 (EJB)  
Jakarta Bean Validation 3.0  
Jakarta Contexts and Dependency Injection 4.0 (CDI)  
Jakarta JSON Binding 3.0 / JSON Processing 2.1  
Jakarta WebSocket 2.1  
Jakarta Mail 2.1  
Jakarta Security 3.0  

### Configuraciones Especiales

**JasperReports**: Librerías de fuentes instaladas (`libfreetype6`, `libfreetype6-dev`)  
**Acceso Externo**: `host="*"` en httpEndpoint  
**DataSource**: Configurado para `db17:5432`  
**Auto-expansión**: WAR se despliega automáticamente  
**SSL**: Certificados autofirmados generados automáticamente  
**Timezone**: America/El_Salvador  
**Modo Headless**: Java configurado para reportes sin GUI  

## Aplicación Cliente Python

Este proyecto incluye una aplicación cliente de escritorio desarrollada en Python que consume la API REST.

### Requisitos
- Python 3.11 o superior
- Aplicación Java corriendo en Docker (puerto 9080)

### Configuración e Inicio

```powershell
# 1. Navegar al directorio de la aplicación cliente
cd C:\Users\TU_USUARIO\Downloads\App-cliente\App-cliente

# 2. Crear entorno virtual (primera vez)
python -m venv venv

# 3. Activar el entorno virtual
.\venv\Scripts\Activate.ps1

# 4. Instalar dependencias (primera vez)
pip install -r requirements.txt

# 5. Configurar el archivo .env
# Asegúrate que tenga:
# API_BASE_URL=http://localhost:9080/resources/v1
# DOCKER_PORT=9080

# 6. Ejecutar la aplicación
python app.py
```

### Verificación de Conexión

La aplicación cliente se conecta a:
- **URL Base**: `http://localhost:9080/resources/v1`
- **Endpoint TipoAlmacen**: `http://localhost:9080/resources/v1/tipo_almacen`

Si la aplicación cliente no puede conectarse:
1. Verifica que el contenedor `inventario-web` esté corriendo: `docker ps`
2. Prueba el endpoint manualmente: `curl http://localhost:9080/resources/v1/tipo_almacen`
3. Revisa el archivo `.env` de la aplicación cliente

### Funcionalidades
- Listar tipos de almacén
- Crear nuevos tipos de almacén
- Editar tipos existentes
- Eliminar tipos de almacén
- Búsqueda por nombre
- Paginación de resultados

## Puertos Expuestos

| Puerto | Protocolo | Descripción |
|--------|-----------|-------------|
| **9080** | HTTP | Aplicación web (principal) |
| **9443** | HTTPS | Aplicación web segura |
| **5432** | TCP | PostgreSQL (solo si usas el servicio db17 del compose) |

---

## 📚 Recursos Adicionales

- [Documentación de OpenLiberty](https://openliberty.io/docs/)
- [Jakarta EE 10 Specification](https://jakarta.ee/specifications/platform/10/)
- [JasperReports Documentation](https://community.jaspersoft.com/documentation)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)

## 🆘 Soporte

Si tienes problemas:
1. Revisa la sección **Troubleshooting** arriba
2. Verifica los logs: `docker logs -f inventario-web`
3. Busca en los logs de PostgreSQL: `docker logs -f db17`
4. Revisa que todos los requisitos previos estén cumplidos

---

**Última actualización**: Noviembre 2025  
**Versión**: 1.0 con soporte completo para JasperReports
