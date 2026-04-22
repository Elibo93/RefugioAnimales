# 📋 Backlog del Proyecto: Refugio de Animales

Este documento contiene la lista detallada de tareas pendientes para completar el proyecto, organizada por bloques funcionales y prioridades.

---

## 🔝 Resumen de Prioridades

| Prioridad | Descripción General |
| :--- | :--- |
| **🔴 CRÍTICO** | Dashboard admin, perfiles, tareas de voluntariado, contratos de adopción, roles duales, exportaciones PDF. |
| **🟡 IMPORTANTE** | "Mis adoptados", pasarela de pago, imágenes en BD, historial médico accesible, UX general. |
| **🟢 MEJORAS** | Internacionalización (i18n), insignias (logros), UX responsive, refactor código. |
| **⚪ OPCIONAL** | Excel, login con Google/Apple, recomendaciones inteligentes. |

---

## 🛠️ Bloques de Trabajo

### 1) Frontend: Reorganización del Dashboard (Admin)
- [ ] **Navegación:** Agrupar en secciones:
  - **Miembros del refugio:** Usuarios, Adoptantes, Voluntarios (con submenú para Tareas).
  - **Adopciones:** Solicitudes y Adopciones Procesadas.
  - **Gestión:** Animales e Historiales Médicos.
- [ ] **Listados:**
  - Cambiar estética de animales a formato lista simple con miniatura.
  - Botón de "Asignar Tareas" en lista de voluntarios.
  - Botón de "Ver Historial Médico" en lista de animales.
  - Hacer clickables los nombres en las listas para ir al perfil.

### 2) Frontend: Perfiles Detallados
- [ ] **Perfil de Persona:** Página exclusiva que muestre info de adoptante y/o voluntario, animales adoptados y seguimiento.
- [ ] **Perfil de Animal:** Página exclusiva con datos completos, historial médico, estadísticas de vistas e info del adoptante.

### 3) Frontend: Panel del Adoptante
- [ ] **"Mis Adoptados" (Sección del Sidebar):** * **Visibilidad de la sección:** Mostrar en el sidebar única y exclusivamente a los usuarios que tengan el **rol de Adoptante** (adquirido tras enviar una solicitud de adopción).
    * **Lógica de visualización del contenido:**
        * **Estado Pendiente o Aprobado:** Si el usuario tiene una o varias solicitudes en curso o ya aprobadas por el administrador, se mostrará la lista con la información del animal (o animales) correspondientes.
        * **Estado Denegado:** Si la solicitud del usuario ha sido rechazada por el administrador (y no tiene otras en curso), se mostrará la pantalla vacía con el mensaje: *"Vaya, parece que aún no te has hecho con ninguno de nuestros amiguitos"* junto con el botón **"Adopta ahora"**.
- [ ] **Notificaciones:** Estados de solicitudes (aprobada/rechazada) y sugerencias de animales.
- [ ] **Encuesta:** Formulario de preferencias para activar recomendaciones inteligentes.

### 4) Frontend/Backend: Panel del Voluntario y Tareas
- [ ] **"Mis Tareas":** Lista de tareas asignadas con estados: **Pendiente, Propuesta, Aceptada, En progreso, Finalizada, Rechazada, Cancelada**.
- [ ] **Gestión de Propuestas:** El voluntario debe poder aceptar o rechazar tareas enviadas por el admin.
- [ ] **Notificaciones:** Tareas nuevas y recordatorios de fechas de vencimiento.

### 5) Backend: Lógica de Adopción y Roles
- [ ] **Dualidad de Roles:** Permitir que un usuario sea Adoptante y Voluntario simultáneamente.
- [ ] **Validaciones:**
  - Impedir adopciones duplicadas del mismo animal.
  - Acceso a funciones de voluntario solo tras aprobación del admin.
- [ ] **Contratos:** Sistema automático de generación y asociación de contratos legales al formalizar la adopción.

### 6) Backend: Notificaciones y Administración
- [ ] **Buzón Admin:** Notificar nuevas solicitudes de voluntariado, adopción y donaciones.
- [ ] **Lógica de Histórico:** Guardar quién cambió el estado de una tarea y cuándo.

### 7) Sistema de Logros (Gamificación)
- [ ] **Donantes:** Insignias según el importe total donado.
- [ ] **Voluntarios:** Méritos por antigüedad y cantidad de tareas completadas.

### 8) Gestión Multimedia y Archivos
- [ ] **Imágenes:** Subida de fotos desde el equipo local y almacenamiento (BD o filesystem).
- [ ] **Exportaciones:** Arreglar PDF de todas las listas. Implementar exportación de contratos. Añadir Excel si es posible.

### 9) Donaciones y Seguridad
- [ ] **Pagos:** Simulación de pasarela de pago (modo prueba).
- [ ] **OAuth:** Registro/Login con Google y Apple (Opcional).

### 10) Calidad y DevOps
- [ ] **i18n:** Traducir toda la app (ES/EN) usando archivos de propiedades.
- [ ] **Docker:** Unificar toda la infraestructura (Gateway, Auth, Backend, Frontend, DB) en un `docker-compose.yml`.
- [ ] **Código:** Refactorizar controladores, unificar mappers y completar JavaDoc.

---
*Este documento es una guía viva para el desarrollo del TFG - Refugio de Animales.*
