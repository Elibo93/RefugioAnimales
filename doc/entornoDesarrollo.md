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

[Volver al README](/README.md)
