# 🛠️ Stack Tecnológico y Versiones: RefugioAnimales

Para garantizar la estabilidad y escalabilidad del sistema, se han seleccionado versiones de largo recorrido (LTS) y las últimas innovaciones estables de los ecosistemas Spring y Web.

## 1. Backend y Núcleo de Microservicios

| Tecnología | Versión | Rol en el Proyecto |
| :--- | :--- | :--- |
| **Java JDK** | **17 (LTS)** | Lenguaje base. Uso extensivo de **Records** para inmutabilidad de datos. |
| **Spring Boot** | **3.4.4** | Framework de orquestación, seguridad y servidor embebido. |
| **Spring Cloud** | **2024.0.0** | Gestión de microservicios (Eureka Discovery & API Gateway). |
| **Spring Data JPA**| **Managed** | Abstracción de la capa de persistencia mediante repositorios. |
| **Hibernate** | **6.6.x** | Motor de mapeo objeto-relacional (ORM) para MySQL. |
| **Liquibase** | **4.27.0** | Gestión y versionado del esquema de base de datos. |
| **MySQL** | **8.3.0** | Motor de base de datos relacional (producción y Docker). |

## 2. Frontend y Experiencia de Usuario

| Tecnología | Versión | Rol en el Proyecto |
| :--- | :--- | :--- |
| **Thymeleaf** | **3.1.x** | Motor de plantillas Server-Side con sistema de fragmentos. |
| **HTMX** | **2.0.4** | Reactividad y peticiones AJAX dinámicas sin JavaScript pesado. |
| **SweetAlert2** | **11.x** | Sistema de alertas visuales y confirmaciones de usuario. |
| **Lucide Icons** | **Latest** | Set de iconos vectoriales ligeros para la interfaz. |
| **Flying Saucer** | **9.1.22** | Generación de documentos PDF (Contratos de Adopción). |

---

## 3. Infraestructura y Operaciones
Utilizamos **Maven** como gestor de ciclo de vida del proyecto multi-módulo y **Docker (v20.x+)** para asegurar la paridad de entornos mediante contenedores aislados.

---
*Documentación técnica - TFG Refugio*
