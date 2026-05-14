# 🚀 Estrategia de Implantación: RefugioAnimales

El despliegue de **RefugioAnimales** se basa en una metodología moderna de microservicios, garantizando que cada componente sea independiente, reproducible y fácil de escalar.

---

## 1. Contenedorización Integral (Docker & Docker Compose)

El sistema está diseñado para ser desplegado bajo una estrategia de **contenedorización total**. Esto significa que no solo la infraestructura de persistencia, sino también cada microservicio, corre de forma aislada y segura:

*   **Microservicios Docketizados:** Cada componente (Eureka, Gateway, Auth, Backend, Frontend) dispone de su propio `Dockerfile`, basado en imágenes ligeras de OpenJDK (JRE), optimizando el tamaño y el tiempo de arranque.
*   **Orquestación Completa:** Mediante un único archivo `docker-compose.yml`, se define la topología completa de la aplicación. Con un solo comando (`docker-compose up`), se levantan los 8 contenedores (6 servicios + 2 bases de datos), gestionando automáticamente sus dependencias y el orden de arranque.
*   **Aislamiento y Redes:** Se crea una red virtual interna de Docker donde los microservicios se comunican de forma segura, exponiendo únicamente el **API Gateway** al tráfico exterior.

## 2. Ciclo de Vida y Empaquetado (Maven)

El proyecto se gestiona como una estructura **Maven Multi-módulo**:

*   **Compilación Unificada:** Mediante el `pom.xml` raíz, se coordinan las dependencias de todos los servicios.
*   **Artefactos Independientes:** Cada microservicio (Eureka, Gateway, Auth, Backend, Frontend) genera su propio "Fat JAR", permitiendo desplegarlos o reiniciarlos de forma individual sin afectar al resto del sistema.
*   **Gestión de Dependencias:** Se utiliza un módulo común (`refugio-common`) para compartir lógica transversal, asegurando la consistencia en el código.

## 3. Despliegue y Configuración Segura

La implantación se apoya en una gestión de configuración externa:

*   **Inyección de Secretos:** Todas las credenciales críticas (passwords, JWT secrets, IPs) se inyectan en tiempo de ejecución mediante un archivo **`.env`**. Esto evita que información sensible se suba al sistema de control de versiones.
*   **Migraciones Automáticas:** Al arrancar el servicio de Backend, **Liquibase** verifica el estado de la base de datos y aplica los cambios de esquema pendientes de forma automática, eliminando la necesidad de scripts SQL manuales durante el despliegue.

## 4. Control de Versiones y Estabilidad

Seguimos una variante de **GitFlow** para proteger la integridad del sistema:

*   **`main`**: Rama de producción. Contiene el código listo para ser desplegado en el refugio.
*   **`develop`**: Rama de integración. Aquí se unen las nuevas funcionalidades antes de pasar a producción.
*   **`feature/*`**: Ramas efímeras para el desarrollo de nuevas capacidades, asegurando que el código en desarrollo nunca rompa la versión estable.

---
*Documentación técnica - TFG Refugio*
