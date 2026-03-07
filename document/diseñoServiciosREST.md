### Diseño de los servicios REST - Gestión de Refugio
---

#### Introducción
La comunicación del sistema se basa en una arquitectura **RESTful** estricta, asegurando que cualquier cliente (Web o futuro Móvil) pueda interactuar con los datos del refugio de forma predecible.

**Principios Adoptados:**
* **Semántica HTTP:** GET (Lectura), POST (Alta), PUT (Actualización), DELETE (Baja).
* **Formato:** JSON (`application/json`).
* **Seguridad:** Protección mediante roles (RBAC) gestionada por Spring Security.

---

#### Estructura de Endpoints (v1)

#### 1. Recursos de Animales
| Operación | Método | Endpoint (URI) | Descripción |
| :--- | :--- | :--- | :--- |
| **Listar** | `GET` | `/api/v1/animales` | Catálogo completo (paginado). |
| **Alta** | `POST` | `/api/v1/animales` | Registro de nuevo residente. |
| **Detalle** | `GET` | `/api/v1/animales/{id}` | Historial y salud del animal. |
| **Baja** | `DELETE` | `/api/v1/animales/{id}` | Salida del animal del sistema. |

#### 2. Recursos de Adopciones
| Operación | Método | Endpoint (URI) | Descripción |
| :--- | :--- | :--- | :--- |
| **Solicitar** | `POST` | `/api/v1/adopciones` | Nueva solicitud de adopción. |
| **Validar** | `PUT` | `/api/v1/adopciones/{id}` | Cambio de estado por administración. |
| **Seguimiento** | `POST` | `/api/v1/adopciones/{id}/seguimientos` | Registro de visita post-adopción. |

#### 3. Recursos de Personal (Voluntarios)
| Operación | Método | Endpoint (URI) | Descripción |
| :--- | :--- | : :--- | :--- |
| **Listar** | `GET` | `/api/v1/voluntarios` | Directorio de colaboradores. |
| **Asignar** | `POST` | `/api/v1/voluntarios/{id}/animales` | Asignación de animales al voluntario. |

---

#### Documentación Interactiva (Swagger UI)
El proyecto expone un endpoint interactivo (vía **OpenAPI 3.0**) que permite probar los servicios directamente desde el navegador, facilitando la integración y el testing del tribunal.

---

[Volver](/README.md)
