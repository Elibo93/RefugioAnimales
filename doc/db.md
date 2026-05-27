### Base de Datos - Arquitectura y Modelo de Datos
---

La capa de persistencia del **Refugio de Animales** está diseñada para ser robusta, escalable y mantenible. Se utiliza un enfoque basado en **Spring Data JPA** con **Hibernate** como motor de persistencia, gestionando el ciclo de vida de los datos mediante **Liquibase**.

---

#### 1. Infraestructura y Entorno (Docker + MySQL)

A diferencia de prototipos iniciales, el proyecto utiliza **MySQL 8.0** de forma consistente tanto en desarrollo como en producción mediante contenedores Docker. Esto asegura la paridad de entornos.

##### Configuración de Conexión
```properties
# Localización: refugio-backend/src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/refugio_db
spring.datasource.username=root
spring.datasource.password=root

# Control de Versiones con Liquibase
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
```

---

#### 2. Modelo de Datos (Diagrama Entidad-Relación)

El sistema utiliza un esquema relacional normalizado que separa las identidades legales de los usuarios y gestiona flujos complejos de adopción, tareas, donaciones y gamificación. El diseño sigue una arquitectura de microservicios, donde las bases de datos de **Auth** y **Backend** están aisladas lógicamente.

```mermaid
erDiagram
    %% --- BASE DE DATOS: REFUGIO_AUTH ---
    subgraph "Base de Datos: Auth"
        usuarios {
            int id PK
            string email UK
            string username UK
            string contrasena
            string rol
            datetime created_at
        }
    end

    %% --- BASE DE DATOS: REFUGIO_BACKEND ---
    subgraph "Base de Datos: Backend"
        animales {
            int id PK
            string nombre
            string especie
            string especie_personalizada
            string raza
            string sexo
            string chip_id UK
            string estado
            int edad
            string tamano
            string descripcion
            string foto
            double peso
            int nivel_energia
            boolean urgencia
            int visitas
            datetime fecha_ingreso
        }

        perfiles_legales {
            int id PK
            int usuario_id FK "UK"
            string nombre
            string apellido
            string dni UK
            string telefono
            string direccion
            string fecha_nacimiento
        }

        adoptantes {
            int id PK
            int usuario_id UK "FK"
            string estado_validacion
            datetime fecha_registro
        }

        voluntarios {
            int id PK
            int usuario_id UK "FK"
            string disponibilidad
            string especialidad
            string status
            datetime created_at
        }

        solicitudes_adopcion {
            int id PK
            int animal_id FK
            int adoptante_id FK
            datetime fecha
            string estado
            string comentario
            string comentario_admin
        }

        adopciones {
            int id PK
            int adoptante_id FK
            int animal_id FK "UK"
            int solicitud_adopcion_id FK
            datetime fecha_adopcion
            string estado
            string contrato
        }

        historial_medicos {
            int id PK
            int id_animal FK
            datetime fecha
            string descripcion
            string tratamiento
            string veterinario
        }

        animal_favorito {
            int id PK
            int usuario_id FK
            int animal_id FK
            datetime fecha_creacion
        }

        tareas {
            int id PK
            string descripcion
            datetime fecha
            string estado
            datetime fecha_limite
            string instrucciones
            boolean notificado_vencimiento
        }

        voluntarios_tareas {
            int tarea_id PK "FK"
            int voluntario_id PK "FK"
        }

        disponibilidad_voluntario {
            int id PK
            int voluntario_id FK
            datetime fecha
            string turno
            string estado
        }

        tarea_historial {
            int id PK
            int tarea_id FK
            int usuario_id FK
            string estado_anterior
            string estado_nuevo
            datetime fecha_cambio
            string observaciones
        }

        donaciones {
            int id PK
            int usuario_id FK
            int objetivo_id FK
            string tipo
            double cantidad
            string frecuencia
            datetime fecha
            datetime proxima_fecha_pago
            string descripcion
        }

        objetivos_donacion {
            int id PK
            string titulo
            string descripcion
            double monto_objetivo
            double monto_recaudado
            string prioridad
            string estado
            datetime fecha_inicio
            datetime fecha_limite
            string icono
        }

        notificaciones {
            int id PK
            int usuario_id FK
            string rol
            string titulo
            string mensaje
            datetime fecha
            boolean leida
            string tipo
            string enlace
        }

        preferencias_adopcion {
            int id PK
            int usuario_id UK "FK"
            int edad_max
            int nivel_energia_max
            boolean notificaciones_activas
            boolean encuesta_omitida
            datetime created_at
            datetime updated_at
        }

        preferencias_especies {
            int preferencia_id FK
            string especie
        }
        preferencias_tamanos {
            int preferencia_id FK
            string tamano
        }
        preferencias_sexos {
            int preferencia_id FK
            string sexo
        }

        gamificacion_logro {
            long id PK
            string codigo UK
            string nombre
            string descripcion
            string categoria
            string requisito_tipo
            decimal requisito_valor
            string icono_lucide
            string rareza
        }

        gamificacion_usuario_logros {
            long usuario_id PK "FK"
            long logro_id PK "FK"
            datetime fecha_desbloqueo
        }

        gamificacion_usuario_metricas {
            long usuario_id PK "FK"
            int tareas_completadas
            decimal total_donado
            datetime fecha_primer_aporte
            datetime ultima_actualizacion
        }
    end

    %% Relaciones Lógicas y Físicas
    usuarios ||--o| perfiles_legales : "perfil"
    usuarios ||--o| adoptantes : "es"
    usuarios ||--o| voluntarios : "es"
    animales ||--o| adopciones : "1:1"
    adoptantes ||--o{ adopciones : "formaliza"
    animales ||--o{ solicitudes_adopcion : "recibe"
    adoptantes ||--o{ solicitudes_adopcion : "presenta"
    animales ||--o{ historial_medicos : "tiene"
    voluntarios }o--o{ tareas : "asignados (N:M)"
    usuarios ||--o{ notificaciones : "recibe"
    usuarios ||--o{ donaciones : "realiza"
    usuarios ||--o{ animal_favorito : "marca"
    objetivos_donacion ||--o{ donaciones : "recibe fondos"
    preferencias_adopcion ||--o{ preferencias_especies : "incluye"
    preferencias_adopcion ||--o{ preferencias_tamanos : "incluye"
    preferencias_adopcion ||--o{ preferencias_sexos : "incluye"
    gamificacion_logro ||--o{ gamificacion_usuario_logros : "obtenido por"
    usuarios ||--o| gamificacion_usuario_metricas : "posee"
    tareas ||--o{ tarea_historial : "rastrea"
    voluntarios ||--o{ disponibilidad_voluntario : "registra turnos"
```

---

#### 3. Gestión de Cambios (Liquibase) y Semillas de Datos

No se utiliza `ddl-auto: update` en producción. En su lugar, todos los cambios de esquema se definen en archivos YAML bajo `src/main/resources/db/changelog/`.

*   **001-initial-schema.yaml**: Creación de tablas base.
*   **002-seed-data.yaml**: Datos maestros de la plataforma (ej. catálogo de animales precargados).

**Gestión Segura de Contraseñas Semilla:**
Para poblar los usuarios de prueba no se insertan contraseñas en texto plano. Se utiliza el archivo `data.sql` (en `refugio-auth`) donde las contraseñas (ej. `password123`) ya están **hasheadas con BCrypt**. Además, el usuario "Admin Supremo" se genera dinámicamente mediante un componente de Spring (`AdminInitializer.java`) para asegurar que su contraseña no dependa de un script estático.

---

#### 4. Estrategia de Almacenamiento de Imágenes

Se ha optado por una estrategia de **Filesystem (Sistema de Archivos)** en lugar de almacenar las imágenes como `BLOB` en la base de datos:

1.  **Rendimiento**: La base de datos se mantiene ligera, acelerando los backups y las consultas.
2.  **Servido Estático**: Las imágenes se sirven directamente por el servidor web/Spring desde la carpeta `uploads/animales/`.
3.  **Referencia en BD**: En la tabla `ANIMALES`, la columna `foto` almacena el nombre del archivo (ej: `luna_e1939c57.jpg`) o la ruta relativa (`/api/v1/animales/images/...`).
4.  **Estandarización**: Los archivos se renombran automáticamente al subir siguiendo el patrón `{nombre}_{uuid}.{ext}` para evitar colisiones.

---

#### 5. Validaciones de Integridad

*   **Chip ID Único**: El `chip_id` es obligatorio y único en la tabla de animales para evitar duplicidad de registros físicos.
*   **DNI Único**: El identificador fiscal en `PERFILES_LEGALES` garantiza que una persona física no se registre varias veces en el sistema.
*   **Identidad de Usuario**: Las columnas `email` y `username` son claves únicas (UK), impidiendo el registro de cuentas duplicadas.
*   **Restricción de Adopción (1:1)**: Un animal solo puede estar asociado a **una** adopción confirmada. El sistema bloquea cualquier intento de duplicar este vínculo.
*   **Integridad Clínica**: Los registros en `HISTORIALES_MEDICOS` requieren obligatoriamente un `animal_id` válido; no se permiten registros médicos sin un paciente asociado.
*   **Validación de Estados**: Las adopciones y solicitudes solo pueden procesarse si el animal se encuentra en estado `DISPONIBLE`. Si el estado cambia a `ADOPTADO` o `RESERVADO`, las solicitudes pendientes quedan bloqueadas.
*   **Asignación de Tareas**: La tabla intermedia `voluntarios_tareas` garantiza la integridad en la asignación de personal, impidiendo que un voluntario sea asignado múltiples veces a la misma tarea específica.
*   **Gestión de Huérfanos (Orphan Removal)**: La eliminación de una entidad principal (como un Animal) desencadena la limpieza automática de sus entidades dependientes (Historiales, Solicitudes) para mantener la base de datos limpia de datos inconsistentes.

---

[Volver](/README.md)
