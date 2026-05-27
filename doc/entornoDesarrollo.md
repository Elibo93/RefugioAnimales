### Entorno de Desarrollo y Stack Tecnológico
---

Para la implementación del sistema del refugio, se ha seleccionado un ecosistema tecnológico moderno, basado en una arquitectura de microservicios distribuida para garantizar escalabilidad y mantenimiento independiente.

#### 🏗️ Arquitectura de Microservicios
*   **Java 17 (LTS)**: Lenguaje base por su robustez y tipado fuerte.
*   **Spring Boot 3.x**: Framework central para la inyección de dependencias y servicios REST.
*   **Spring Cloud Netflix (Eureka)**: Servidor de descubrimiento para la orquestación de servicios.
*   **Spring Cloud Gateway**: Punto de entrada único para el enrutamiento y seguridad.

#### 🗄️ Persistencia y Gestión de Datos
*   **Spring Data JPA / Hibernate**: ORM para la comunicación con las bases de datos.
*   **MySQL 8.0**: Motor de base de datos relacional (uno por cada microservicio con datos).
*   **Liquibase**: Sistema de control de versiones para la base de datos (migraciones seguras).
*   **Docker & Docker Compose**: Contenedorización de toda la infraestructura y servicios de la aplicación.

#### 🎨 Frontend y Experiencia de Usuario
*   **Thymeleaf**: Motor de plantillas server-side.
*   **HTMX**: Inyección de dinamismo y AJAX sin necesidad de frameworks pesados (React/Angular).
*   **Vanilla CSS**: Diseño personalizado, moderno y responsive.

#### 🔐 Seguridad
*   **Spring Security**: Control de acceso basado en roles (RBAC).
*   **JWT (JSON Web Tokens)**: Gestión de sesiones stateless para la comunicación entre servicios.

---

## 🚀 Guía de Arranque Local (Onboarding)

Esta sección está diseñada para levantar el código fuente del proyecto en un entorno de desarrollo local (IDE como IntelliJ, Eclipse o VSCode).

### 1. Requisitos Previos
* **Java 17 (JDK):** Versión requerida para la compilación.
* **MySQL Server (8.0+):** Para las bases de datos locales. (Si prefieres no instalar MySQL, revisa el [Despliegue con Docker](despliegue.md) o el perfil `dev` en [Gestión de Perfiles](gestiónPerfiles.md)).

### 2. Variables de Entorno (`.env`)
1. En la raíz del proyecto copia el archivo `.env.example` y renómbralo a **`.env`**.
2. Rellena los valores correspondientes (como `DB_USER` y `DB_PASSWORD`).
> **Nota:** La mayoría de los IDEs modernos detectan automáticamente el archivo `.env` al arrancar.

### 3. Configuración de las Bases de Datos
Debes crear los esquemas vacíos en tu MySQL local antes de arrancar los servicios (Liquibase se encargará automáticamente de crear las tablas):
```sql
CREATE DATABASE auth_db;
CREATE DATABASE backend_db;
```

### 4. Orden de Arranque Estricto
Dado que es una arquitectura de microservicios, **el orden de ejecución es muy importante**. Arranca las aplicaciones (`*Application.java`) así:

1. **Eureka Server (`eureka-server` - Puerto 8761):** Servidor de descubrimiento. Todos los demás servicios buscarán conectarse a él.
2. **Auth (`refugio-auth` - 8081) y Backend (`refugio-backend` - 8082):** Servicios de negocio. Se conectan a MySQL, ejecutan Liquibase y se registran en Eureka.
3. **API Gateway (`api-gateway` - 8080):** Puerta de enlace. Necesita que Auth y Backend estén listos.
4. **Frontend UI (`refugio-frontend` - 8083):** Aplicación web que interactúa con el usuario.

### 5. ¡Todo listo!
Una vez que los 5 microservicios estén en ejecución sin errores, accede a:
🔗 **[http://localhost:8083](http://localhost:8083)**

---

[Volver al README](/README.md)
