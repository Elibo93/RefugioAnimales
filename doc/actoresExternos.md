# 🎭 Actores del Sistema y Casos de Uso
---

Este documento detalla los diferentes perfiles que interactúan con la plataforma del **Refugio de Animales**, sus responsabilidades y los casos de uso asociados a cada rol.

## 🗺️ Flujo de Interacción

Para entender cómo se relacionan los actores con el sistema, este es el esquema de dependencias:

*   **Visitante** ➜ Consulta el **Catálogo** y se **Registra** para convertirse en Adoptante o Voluntario.
*   **Administrador** ➜ Supervisa a **Usuarios/Voluntarios** y gestiona las **Solicitudes**.
*   **Administrador** ➜ Asigna **Tareas** específicas a los **Voluntarios**.
*   **Voluntario** ➜ Actualiza el **Estado/Salud** del **Animal** y completa sus **Tareas**.
*   **Adoptante** ➜ Consulta el **Catálogo** y envía **Solicitudes de Adopción**.
*   **Sistema** ➜ Envía **Notificaciones** automáticas a todos los perfiles según sus acciones.

---

## 👤 Perfiles de Usuario

### 1. Visitante (Público no registrado)
Es cualquier usuario que accede a la web sin haber iniciado sesión.
*   **Consulta de Catálogo:** Visualización de los animales disponibles para adopción.
*   **Búsqueda y Filtros:** Uso de filtros básicos para conocer a los animales del refugio.
*   **Información del Refugio:** Acceso a datos de contacto, ubicación y cómo colaborar.
*   **Registro:** Posibilidad de crear una cuenta para convertirse en **Adoptante** o solicitar ser **Voluntario**.

### 2. Administrador (Gestión Global)
Es el responsable de la integridad del sistema y la supervisión de la operativa diaria.
*   **Gestión de Inventario:** Altas, bajas y modificaciones de animales y sus perfiles.
*   **Supervisión de Miembros:** Gestión de usuarios, validación de nuevos voluntarios y adoptantes.
*   **Control de Adopciones:** Revisión de solicitudes, aprobación/rechazo y formalización de contratos.
*   **Gestión de Operativa:** Asignación de tareas específicas a voluntarios.
*   **Buzón de Notificaciones:** Centro de control para nuevas solicitudes y donaciones recibidas.
*   **Finanzas:** Registro y seguimiento de donaciones y pasarela de pagos.

### 2. Voluntario (Operativo)
Perfil interno encargado del bienestar directo de los animales.
*   **Cuidado Animal:** Registro de incidencias y actualización del estado de salud de los animales.
*   **Historial Médico:** Creación y mantenimiento de entradas médicas detalladas.
*   **Sistema de Tareas:** Gestión de su lista de tareas personal (aceptar, rechazar y marcar progreso).
*   **Dualidad:** Un voluntario puede ser también adoptante, manteniendo ambos paneles de gestión activos.

### 3. Adoptante (Usuario Público)
Cualquier ciudadano interesado en la adopción responsable.
*   **Catálogo Interactivo:** Búsqueda y filtrado de animales por especie, edad, tamaño o estado.
*   **Proceso de Adopción:** Envío de solicitudes y seguimiento del estado de las mismas en tiempo real.
*   **Panel Personal:** Sección "Mis Adoptados" para ver el histórico de animales a su cargo.
*   **Notificaciones:** Recepción de alertas sobre el estado de sus solicitudes y sugerencias personalizadas.

---

## 🔐 Seguridad y Acceso

El sistema implementa un control de acceso robusto basado en **Spring Security** y **JWT** (JSON Web Tokens), asegurando que cada actor acceda únicamente a sus recursos:

| Rol | Nivel de Acceso | Tecnologías |
| :--- | :--- | :--- |
| **PÚBLICO** | Lectura del Catálogo | Acceso Libre |
| **ADMIN** | Acceso Total (Backoffice) | REST API, OAuth2 |
| **VOLUNTARIO** | Gestión de Animales y Tareas | REST API |
| **ADOPTANTE** | Perfil Público y Solicitudes | REST API |

---

[⬅️ Volver al README](/README.md)