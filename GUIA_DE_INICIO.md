# 🚀 Guía de Inicio Rápido y Pruebas

Este documento está diseñado para que cualquier profesor o desarrollador pueda levantar el sistema y probar todas las funcionalidades del Refugio de Animales en pocos minutos.

## 📋 Requisitos Previos

Solo necesitas tener instalado:
*   **Docker** y **Docker Compose** en tu máquina (Windows, macOS o Linux).
*   *No es necesario tener instalado Java, Maven o MySQL de forma local, ¡Docker se encarga de todo!*

---

## 🛠️ Cómo Lanzar la Aplicación

1. **Abrir una terminal** y situarse en la raíz del proyecto (donde se encuentra el archivo `docker-compose.yml`).
2. **Ejecutar el siguiente comando** para descargar las imágenes y levantar todos los microservicios:
   ```bash
   docker compose up -d
   ```
   *(La primera vez puede tardar un par de minutos mientras se descargan las imágenes desde Docker Hub).*

3. **Verificar que están corriendo** (opcional):
   ```bash
   docker compose ps
   ```
   Deberías ver contenedores como `api_gateway`, `eureka_server`, `refugio_frontend`, `refugio_backend`, `refugio_auth` y las bases de datos de MySQL.

---

## 🌐 Acceso a la Plataforma

Una vez levantados los contenedores, abre tu navegador web (preferiblemente Chrome o Firefox) y accede a la siguiente dirección:

👉 **[https://localhost:8443/web/home](https://localhost:8443/web/home)**

> [!WARNING]  
> **Aviso de Certificado de Seguridad:**  
> Al usar `https` en un entorno local de pruebas (localhost) con un certificado autofirmado, tu navegador te advertirá de que "La conexión no es privada".  
> Esto es un comportamiento normal. Para continuar, haz clic en **"Configuración Avanzada"** y luego en **"Continuar a localhost (no seguro)"**.

---

## 🔑 Usuarios de Prueba (Test Data)

La base de datos se inicializa automáticamente con datos de prueba reales para que puedas probar la aplicación sin necesidad de registrarte. 

Puedes usar cualquiera de los siguientes usuarios:

| Rol | Correo Electrónico | Contraseña | ¿Qué puede hacer? |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@refugio.es` | `admin123` | Tiene acceso total al panel de administración, gestión de staff, adopciones y finanzas. |
| **Voluntario / Staff** | `laura@mail.com` | `password123` | Puede gestionar animales, visitas, tareas y ver el historial médico. |
| **Voluntario / Staff** | `carlos@mail.com` | `password123` | Puede gestionar animales, visitas, tareas y ver el historial médico. |
| **Adoptante** | `mario@mail.com` | `password123` | Puede navegar por los animales disponibles, ver sus favoritos y solicitar adopciones. |

*(Nota: Todos los usuarios pregenerados por los scripts que terminan en `@mail.com` comparten la contraseña `password123`).*

---

## 🛑 Cómo Detener la Aplicación

Cuando hayas terminado de hacer las pruebas, puedes apagar todos los servicios de forma limpia ejecutando en la terminal (desde la raíz del proyecto):

```bash
docker compose down
```

*(Si deseas borrar también los datos guardados en la base de datos para empezar de cero la próxima vez, ejecuta `docker compose down -v`)*.
