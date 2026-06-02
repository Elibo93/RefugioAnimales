### Organización de Paquetes y Arquitectura - Refugio
---

La arquitectura del sistema se basa en una **Arquitectura de Microservicios** distribuida, donde cada módulo sigue internamente los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)**. Esta organización asegura que la lógica de negocio (dominio) esté aislada de las tecnologías externas (bases de datos, APIs de terceros, interfaces web).

---

#### 1. Estructura Global (Ecosistema de Microservicios)
El proyecto se divide en módulos independientes, cada uno con una responsabilidad clara:

- **`api-gateway`**: Punto de entrada único que gestiona el enrutamiento y la seguridad perimetral.
- **`eureka-server`**: Servidor de descubrimiento para que los microservicios se localicen dinámicamente.
- **`refugio-auth`**: Microservicio dedicado a la seguridad (JWT), gestión de usuarios y roles.
- **`refugio-backend`**: El núcleo del negocio, que gestiona animales, adopciones y tareas.
- **`refugio-frontend`**: Interfaz de usuario servida mediante Thymeleaf y dinamizada con HTMX.
- **`refugio-common`**: Librería compartida con modelos, excepciones y utilidades transversales.

---

#### 2. Organización Interna (Clean Architecture)
Dentro de los microservicios core (`backend`, `auth`), seguimos este esquema de paquetes:

```text
es.refugio.[modulo]
├── domain                  # CAPA DE DOMINIO (El "corazón")
│   ├── model               # Entidades puras (Animal, Persona, Tarea).
│   ├── repository          # Interfaces de repositorio (Puertos de salida).
│   └── service             # Lógica de negocio pura e invariantes.
├── application             # CAPA DE APLICACIÓN (Casos de Uso)
│   ├── usecase             # Implementación de flujos (ej: RegistrarAdopcion).
│   └── dto                 # Objetos de transferencia de datos.
└── infrastructure          # CAPA DE INFRAESTRUCTURA (Adaptadores)
    ├── inbound             # Controladores REST / Web (Adaptadores de entrada).
    ├── outbound            # Implementación de persistencia (JPA, SQL).
    ├── security            # Configuración de seguridad y filtros JWT.
    └── config              # Beans de configuración de Spring.
```

---

#### 3. Integración y Comunicación
- **Síncrona (REST):** Los servicios se comunican mediante peticiones HTTP internas, utilizando nombres de servicio registrados en Eureka.
- **Dinamismo HTMX:** El microservicio `frontend` utiliza HTMX para realizar peticiones parciales al `gateway`, que luego las redirige al `backend`, permitiendo actualizaciones de la UI sin recargar la página.
- **Lógica Compartida:** El uso de `refugio-common` garantiza que todos los servicios hablen el mismo "idioma" técnico, evitando la duplicidad de código en validaciones y mappers.

---

#### 4. Contenerización (Docker)
Cada uno de los módulos mencionados (`gateway`, `auth`, `backend`, `frontend`, `eureka`) es una unidad de despliegue independiente empaquetada como una **Imagen de Docker**.
- **Aislamiento:** Cada contenedor tiene su propio entorno de ejecución (JRE 17), evitando conflictos de dependencias entre servicios.
- **Orquestación:** El archivo `docker-compose.yml` en la raíz del proyecto coordina el levantamiento de todos estos contenedores, asegurando que las bases de datos estén listas antes de que los servicios intenten conectarse.
- **Escalabilidad Horizontal:** Gracias a esta estructura de paquetes y Docker, podríamos levantar múltiples instancias del contenedor `refugio-backend` tras el `gateway` para repartir la carga si el número de animales registrados aumentara drásticamente.

---

#### 5. Beneficios de esta Estructura
1.  **Mantenibilidad:** Los cambios en la base de datos (outbound) no afectan a la lógica de negocio (domain).
2.  **Testabilidad:** Es extremadamente sencillo realizar tests unitarios del dominio sin necesidad de levantar Spring o una base de datos.
3.  **Portabilidad:** Gracias a **Docker**, el sistema se comporta de forma idéntica en el ordenador del desarrollador, en el servidor del refugio o en la nube.

---

[Volver al Índice de Documentación](/README.md)


