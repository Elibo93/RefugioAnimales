# 📡 Diseño de Servicios REST (API v1)
---

La plataforma del **Refugio de Animales** expone una interfaz programática (API REST) que permite la comunicación entre el Frontend, los microservicios de Auth y Backend, y posibles integraciones externas.

### 🛠️ Estándares Utilizados
*   **Formato de Intercambio**: JSON.
*   **Prefijo de Rutas**: `/api/v1/`
*   **Autenticación**: JWT (vía Cookie `JWT_TOKEN` o Header `Authorization`).
*   **Códigos de Estado**:
    *   `200 OK` / `201 Created` / `204 No Content`
    *   `400 Bad Request` (Error de validación)
    *   `401 Unauthorized` (Falta token)
    *   `403 Forbidden` (Permisos insuficientes)
    *   `404 Not Found` (Recurso inexistente)

---

### 1. Gestión de Seguridad y Usuarios (Auth)
Gestionado por el microservicio `refugio-auth`.

| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/usuarios/publico` | Registro de nuevo usuario público. | Anónimo |
| `POST` | `/api/v1/usuarios` | Alta de usuario (uso administrativo). | `ADMIN` |
| `GET` | `/api/v1/usuarios` | Listado completo de usuarios. | `ADMIN` |
| `PUT` | `/api/v1/usuarios/{id}/rol` | Cambio de rol y refresco de Token JWT. | `ADMIN` |
| `DELETE` | `/api/v1/usuarios/{id}` | Eliminación de cuenta. | `ADMIN` |

---

### 2. Inventario de Animales
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/animales` | Catálogo filtrable (especie, sexo, edad, etc). | Público |
| `GET` | `/api/v1/animales/{id}` | Ficha técnica completa del animal. | Público |
| `POST` | `/api/v1/animales` | Alta de animal (incluye subida de imagen). | `ADMIN` |
| `PUT` | `/api/v1/animales/{id}` | Actualización de perfil del animal. | `ADMIN` |
| `DELETE` | `/api/v1/animales/{id}` | Baja definitiva (cascada en solicitudes). | `ADMIN` |
| `GET` | `/api/v1/animales/especies` | Listado de especies activas en el refugio. | Público |
| `GET` | `/api/v1/animales/images/{file}` | Servicio de imágenes (Filesystem). | Público |

---

### 3. Búsqueda y Filtros Avanzados
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/animales/buscar` | Búsqueda dinámica por múltiples criterios. | Público |
| `GET` | `/api/v1/animales/favoritos` | TOP animales con más interacciones. | Público |
| `GET` | `/api/v1/personas/buscar` | Búsqueda parcial de perfiles por nombre. | `ADMIN` |

---

### 3. Procesos de Adopción
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/solicitudes-adopcion/directa` | Envío de solicitud para usuarios registrados. | `ADOPTANTE` |
| `POST` | `/api/v1/solicitudes-adopcion/publico/registro-y-adopcion` | Registro y solicitud en un solo paso. | Anónimo |
| `GET` | `/api/v1/solicitudes-adopcion` | Listado (filtra por dueño si no es staff). | Todos |
| `POST` | `/api/v1/solicitudes-adopcion/{id}/aprobar` | Formalizar adopción (mueve animal a RESERVADO). | `ADMIN` |
| `POST` | `/api/v1/solicitudes-adopcion/{id}/rechazar` | Cierre de solicitud con comentario. | `ADMIN` |

---

### 4. Preferencias de Adopción
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/preferencias-adopcion/usuario/{id}` | Recupera las preferencias guardadas del usuario. | `ADOPTANTE` |
| `POST` | `/api/v1/preferencias-adopcion` | Guarda o actualiza las preferencias de tamaño, edad, etc. | `ADOPTANTE` |

---

### 4. Operativa y Tareas (Staff)
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/tareas` | Listado de tareas activas. | `ADMIN`, `VOLUN` |
| `POST` | `/api/v1/tareas` | Creación y asignación de nueva tarea. | `ADMIN` |
| `PUT` | `/api/v1/tareas/{id}` | Actualización de estado/progreso. | `ADMIN`, `VOLUN` |
| `GET` | `/api/v1/voluntarios` | Directorio de personal operativo. | `ADMIN` |
| `POST` | `/api/v1/voluntarios/{id}/disponibilidad` | Añade turnos y días de disponibilidad para un voluntario. | `ADMIN`, `VOLUN` |
| `GET` | `/api/v1/voluntarios/{id}/disponibilidad` | Obtiene el calendario de disponibilidad de un voluntario. | `ADMIN`, `VOLUN` |
| `DELETE` | `/api/v1/voluntarios/{id}/disponibilidad/{fecha}` | Elimina la disponibilidad de un día concreto. | `ADMIN`, `VOLUN` |
| `GET` | `/api/v1/historiales-medicos/animal/{id}` | Evolución clínica del animal. | `VOLUN`, `ADMIN` |

---

### 5. Sistema de Notificaciones y Donaciones
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/notificaciones/me` | Listado personal de alertas. | Logueado |
| `GET` | `/api/v1/notificaciones/me/count` | Contador de alertas no leídas (Poller). | Logueado |
| `PUT` | `/api/v1/notificaciones/{id}/leer` | Marcar notificación como procesada. | Logueado |
| `POST` | `/api/v1/donaciones` | Registro de nueva aportación económica. | `ADOPTANTE` |
| `GET` | `/api/v1/objetivos-donacion` | Proyectos activos para financiar. | Público |

---

### 6. Sistema de Gamificación y Logros
| Método | Endpoint | Descripción | Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/gamificacion/metricas/me` | Obtiene las métricas actuales del usuario logueado. | Logueado |
| `GET` | `/api/v1/gamificacion/logros` | Listado de todos los logros disponibles en la plataforma. | Logueado |
| `GET` | `/api/v1/gamificacion/logros/me` | Obtiene los logros desbloqueados por el usuario. | Logueado |

---

### 🚀 Documentación Interactiva (Swagger/OpenAPI)
Para consultar los esquemas de datos (JSON) y probar los endpoints en tiempo real:
*   **URL**: `http://localhost:8080/swagger-ui.html` (vía API Gateway).
*   **OpenAPI Spec**: `/v3/api-docs`

---

[Volver](/README.md)
