### Despliegue e Infraestructura (Docker)
---

El sistema del **Refugio de Animales** está diseñado bajo una arquitectura de microservicios totalmente **contenedorizada**. Esto garantiza la portabilidad, escalabilidad y facilidad de despliegue en cualquier entorno (desarrollo, pruebas o producción).

---

#### 1. Arquitectura de Microservicios
La plataforma se compone de varios servicios independientes que colaboran entre sí, orquestados mediante **Docker Compose**.

| Servicio | Contenedor | Puerto Interno | Descripción |
| :--- | :--- | :--- | :--- |
| **Eureka Server** | `refugio-eureka` | `8761` | Registro y descubrimiento de servicios. |
| **API Gateway** | `refugio-gateway` | `8080` | Punto de entrada único y balanceo de carga. |
| **Auth Service** | `refugio-auth` | `8081` | Seguridad, JWT y gestión de roles. |
| **Backend Service** | `refugio-backend` | `8082` | Lógica de negocio y dominio animal. |
| **Frontend UI** | `refugio-frontend` | `8083` | Interfaz de usuario (Thymeleaf/HTMX). |
| **DB Auth** | `mysql-auth` | `3306` | Persistencia de seguridad. |
| **DB Backend** | `mysql-backend` | `3306` | Persistencia de negocio. |

---

#### 2. Orquestación con Docker Compose
Toda la infraestructura se levanta con un único comando, eliminando la necesidad de configurar Java o MySQL localmente.

##### Comandos Principales:
```bash
# Levantar todo el ecosistema en segundo plano
docker compose up -d

# Ver el estado de los servicios
docker compose ps

# Ver logs en tiempo real de un servicio específico
docker compose logs -f refugio-backend

# Detener y eliminar contenedores y redes
docker compose down
```

---

#### 3. Persistencia de Datos y Archivos
Para evitar la pérdida de información al reiniciar los contenedores, se utilizan **Volúmenes de Docker**:

*   **Volúmenes de DB:** Los datos de MySQL se almacenan en volúmenes persistentes definidos en el host.
*   **Almacenamiento de Imágenes:** Las fotos de los animales se guardan en un volumen compartido mapeado a `refugio-backend/uploads`. Esto asegura que las imágenes no se pierdan tras un `docker compose down`.

---

#### 4. Configuración de Red (Docker Network)
Los microservicios se comunican a través de una red virtual interna denominada `refugio-network`. 
- El **API Gateway** es el único componente expuesto al exterior (puerto 8080).
- Las bases de datos y los servicios internos no son accesibles desde fuera de la red de Docker por seguridad, a menos que se mapeen puertos específicos en el `docker-compose.yml`.

---

#### 5. Flujo de Despliegue Típico
1.  **Build:** Se generan los archivos `.jar` de cada microservicio mediante Maven.
2.  **Image Creation:** Se construyen las imágenes de Docker (usando los `Dockerfile` de cada módulo).
3.  **Deployment:** `docker compose up -d` despliega la nueva versión.

---

[Volver al Índice de Documentación](/README.md)
