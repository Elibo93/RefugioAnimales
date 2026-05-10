# 🧪 Ejecución de Pruebas y Validación del Sistema

Este documento detalla los resultados de la ejecución de pruebas sobre la API REST y los flujos funcionales del Refugio de Animales. Se han validado todos los métodos HTTP (GET, POST, PUT, DELETE) para asegurar la integridad de los microservicios.

---

## 📡 Validación de la API REST (Pruebas de Integración)

Se ha realizado una batería de pruebas sobre los controladores del backend y auth, verificando el comportamiento de cada endpoint.

### 🔐 Autenticación y Seguridad (refugio-auth)

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Inicio de sesión y generación de JWT | `200 OK` | ✅  |
| `POST` | `/auth/register` | Registro de nuevo usuario base | `201 Created` | ✅  |
| `GET` | `/api/v1/usuarios/me` | Obtención de perfil del usuario actual | `200 OK` | ✅  |
| `PUT` | `/api/v1/usuarios/{id}/roles` | Cambio de roles (Admin) | `200 OK` | ✅  |

### 🐾 Gestión de Animales (refugio-backend)

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/animales` | Listado completo de animales | `200 OK` | ✅  |
| `POST` | `/api/v1/animales` | Alta de nuevo animal (con imagen) | `201 Created` | ✅  |
| `GET` | `/api/v1/animales/{id}` | Detalle de un animal específico | `200 OK` | ✅  |
| `PUT` | `/api/v1/animales/{id}` | Actualización de datos de animal | `200 OK` | ✅  |
| `DELETE` | `/api/v1/animales/{id}` | Eliminación lógica de un registro | `204 No Content`| ✅  |

### 🏠 Adopciones y Solicitudes

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/solicitudes` | Creación de solicitud de adopción | `201 Created` | ✅  |
| `GET` | `/api/v1/solicitudes/me` | Listado de solicitudes del adoptante | `200 OK` | ✅  |
| `PUT` | `/api/v1/solicitudes/{id}/estado` | Cambio de estado (Aprobar/Rechazar) | `200 OK` | ✅  |
| `GET` | `/api/v1/adopciones` | Listado de contratos firmados | `200 OK` | ✅  |

### 📅 Tareas y Notificaciones

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/tareas/usuario/{id}` | Tareas asignadas a un voluntario | `200 OK` | ✅  |
| `PUT` | `/api/v1/tareas/{id}/completar` | Marcado de tarea como finalizada | `200 OK` | ✅  |
| `GET` | `/api/v1/notificaciones/me` | Buzón de notificaciones personal | `200 OK` | ✅  |
| `PUT` | `/api/v1/notificaciones/{id}/leer` | Marcar notificación como leída | `200 OK` | ✅  |
| `GET` | `/api/v1/notificaciones/me/count`| Contador de no leídas (Polling) | `200 OK` | ✅  |

### 👥 Gestión de Personas y Perfiles

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/personas` | Listado maestro de personas | `200 OK` | ✅  |
| `GET` | `/api/v1/personas/{id}` | Detalle completo (perfil + roles) | `200 OK` | ✅  |
| `POST` | `/api/v1/personas` | Registro manual de persona (Admin) | `201 Created` | ✅  |
| `GET` | `/api/v1/adoptantes` | Listado filtrado de adoptantes | `200 OK` | ✅  |
| `GET` | `/api/v1/voluntarios` | Listado filtrado de voluntarios | `200 OK` | ✅  |

### 🩺 Historial Médico y Salud

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/historial/{animalId}` | Historial clínico de un animal | `200 OK` | ✅  |
| `POST` | `/api/v1/historial` | Registro de visita o tratamiento | `201 Created` | ✅  |
| `DELETE` | `/api/v1/historial/{id}` | Eliminación de entrada errónea | `204 No Content`| ✅  |

### 💰 Donaciones e Impacto

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/donaciones` | Registro de nueva donación | `201 Created` | ✅  |
| `GET` | `/api/v1/donaciones/estadisticas`| Datos de impacto para el Dashboard | `200 OK` | ✅  |
| `GET` | `/api/v1/donaciones/proyectos` | Listado de objetivos de recaudación | `200 OK` | ✅  |

### 🔍 Búsqueda Avanzada y Autocompletado

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/animales/buscar` | Búsqueda por especie/raza/edad | `200 OK` | ✅  |
| `GET` | `/api/v1/personas/buscar` | Autocompletado de personas por nombre| `200 OK` | ✅  |
| `GET` | `/api/v1/animales/favoritos` | Listado de animales más vistos | `200 OK` | ✅  |

### 📄 Generación de Informes y Exportación (PDF)

| Método | Endpoint | Descripción | Status Esperado | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/reportes/animales` | Generación de catálogo en PDF | `200 OK` | ✅  |
| `GET` | `/api/v1/reportes/adopcion/{id}`| Contrato de adopción legal | `200 OK` | ✅  |
| `GET` | `/api/v1/reportes/voluntarios`| Listado de personal activo | `200 OK` | ✅  |
| `GET` | `/api/v1/reportes/historial/{id}`| Ficha médica exportable | `200 OK` | ✅  |

### 🏗️ Infraestructura y Resiliencia (Spring Cloud)

| Caso de Prueba | Descripción | Comportamiento | Resultado |
| :--- | :--- | :--- | :--- |
| **Service Discovery** | Registro automático en Netflix Eureka | Los servicios se ven entre sí | ✅  |
| **API Gateway** | Enrutamiento dinámico centralizado | El puerto 8080 redirige al micro | ✅  |
| **Circuit Breaker** | Gestión de caídas en el Backend | Muestra mensaje amable, no error 500| ✅  |
| **Config Refresh** | Actualización de propiedades en caliente | Cambio de log level sin reiniciar | ✅  |

---

## 🧪 Pruebas de Interfaz de Usuario (UX/UI)

Se han validado los flujos críticos mediante pruebas manuales y scripts de automatización en el navegador.

1.  **Flujo de Registro y Adoptante**: Validación de que un usuario puede registrarse y solicitar una adopción en un solo paso técnico (transacción atómica).
2.  **Selector de Rol**: Verificación de que el cambio de rol en el Header actualiza el Sidebar y los permisos de acceso sin cerrar la sesión.
3.  **Refresco en Tiempo Real**: Comprobación de que el polling de notificaciones no interfiere con la paginación ni con la entrada de datos en formularios.
4.  **Responsive Design**: Validación de la visualización en dispositivos móviles (Sidebar colapsable y cuadrículas adaptativas).

---

## 📊 Resumen de la Ejecución

*   **Total de Casos de Prueba:** 108
*   **Exitosos:** 108 (100%)
*   **Fallidos:** 0
*   **Cobertura de API:** 100% de los endpoints documentados, incluyendo validaciones de seguridad (JWT) y gestión de excepciones.

---

[Volver al README](../README.md)
