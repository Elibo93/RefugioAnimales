# ⚙️ Gestión de Perfiles y Entornos: RefugioAnimales

El sistema **RefugioAnimales** utiliza una arquitectura de microservicios basada en Spring Boot, donde la configuración se adapta dinámicamente al entorno de ejecución mediante **Perfiles de Spring** y **Variables de Entorno**.

---

## 1. Estrategia de Configuración

La configuración se distribuye en tres niveles de prioridad para asegurar flexibilidad y seguridad:

1.  **`application.properties` (Configuración Base):**
    *   Contiene la lógica común a todos los entornos.
    *   Define la conectividad con **Eureka Server** (puerto 8761).
    *   Configura **Liquibase** como motor de migraciones para asegurar que el esquema de la base de datos sea idéntico en todos los nodos.
    *   Gestiona la estrategia de headers (`X-Forwarded-*`) para la integración con el **API Gateway**.

2.  **Variables de Entorno (`.env`):**
    *   Se utiliza para desacoplar los secretos del código fuente.
    *   Define credenciales de DB, claves JWT (`JWT_SECRET`) y parámetros de expiración.
    *   Permite que el mismo binario se ejecute en local o en Docker sin cambios de código.

3.  **Perfiles Específicos:**
    *   **`default` (Local / MySQL):** Configuración por defecto para desarrollo con base de datos MySQL compartida.
    *   **`dev` (Aislado / H2):** Perfil de desarrollo rápido. Utiliza base de datos **H2 en memoria**, habilita la consola `/h2-console` y desactiva las cachés de Thymeleaf.
    *   **`prod` (Producción / Docker):** Optimizado para rendimiento. Activa caché de plantillas, reduce el nivel de logs a `ERROR` y utiliza validación estricta de esquema (`ddl-auto=validate`).

---

## 2. Arquitectura de Red y Seguridad

A diferencia de aplicaciones monolíticas, la seguridad perimetral se gestiona de forma centralizada:

*   **SSL (HTTPS):** Solo está habilitado en el **API Gateway** (puerto 8443).
*   **Tráfico Interno:** Los microservicios (Backend, Auth) se comunican mediante HTTP puro en la red interna de Docker o local para optimizar el rendimiento.
*   **Descubrimiento:** Cada servicio se registra en Eureka, lo que permite al Gateway balancear el tráfico y resolver nombres de servicio en lugar de IPs fijas.

---

## 3. Guía de Inicio Rápido para Desarrolladores

### Modo Desarrollo Rápido (Sin MySQL)
Si solo quieres probar cambios en la UI o lógica simple:
```bash
# Activa el perfil dev en el arranque
-Dspring.profiles.active=dev
```
*Acceso a DB: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)*

### Modo Integración (Con Docker)
Para probar el sistema completo con persistencia real:
1. Asegúrate de tener el archivo `.env` configurado.
2. Levanta la infraestructura: `docker-compose up -d mysql eureka-server`.
3. Ejecuta el backend sin perfiles adicionales (tomará la configuración de `application.properties`).

---

## 4. Gestión de Datos con Liquibase
El proyecto utiliza **Liquibase** para evitar discrepancias en la base de datos:
*   **Changelogs:** Ubicados en `src/main/resources/db/changelog/`.
*   **Datos de Prueba:** Se cargan automáticamente mediante contextos de Liquibase para asegurar un entorno poblado al iniciar.
---
*Documentación técnica - TFG Refugio*
