### Requisitos Funcionales (RF) - Refugio de Animales
---

En este apartado se detallan las funcionalidades principales que el sistema del **Refugio de Animales** debe ofrecer. Estos requisitos definen el comportamiento esperado de la aplicación para garantizar una gestión integral de los residentes, el equipo humano y los procesos legales de adopción.

---

#### 1. Actor: Sistema (Requisitos Transversales)
Funcionalidades automáticas e integridad del sistema.

*   **RF-SYS-01: Autenticación y Autorización:** El sistema permitirá el registro y login tradicional de usuarios (email/contraseña) y el inicio de sesión federado alternativo mediante **Google OAuth2** (Single Sign-On). El acceso se gestionará de forma unificada mediante cookies JWT cifradas y control de roles basado en políticas (RBAC).
*   **RF-SYS-02: Validación de Datos:** Validación automática de formatos (DNI/NIE, Email, Teléfono) y campos obligatorios en todos los formularios.
*   **RF-SYS-03: Buscador, Filtros y Paginación:** Capacidad de búsqueda global y filtrado dinámico en catálogos de animales, voluntarios y solicitudes. Todos los listados de datos deben estar paginados desde el servidor mediante el componente genérico `PaginatedResponse<T>` para evitar la sobrecarga de memoria y optimizar el rendimiento, trayendo la información fragmentada bajo demanda.
*   **RF-SYS-04: Notificaciones en Tiempo Real:** El sistema notificará a los usuarios sobre cambios en el estado de sus solicitudes, nuevas tareas asignadas o alertas de urgencia médica.
*   **RF-SYS-05: Auditoría Básica:** Registro automático de la fecha, hora y usuario que realiza cambios críticos (ej: cambio de estado de un animal).

---

#### 2. Actor: Administrador (Control Total)
Gestión estratégica y supervisión de la operativa.

*   **RF-ADM-01: Gestión Maestra de Animales:** Operaciones CRUD completas sobre la ficha del animal, incluyendo carga de imágenes y asignación de microchip.
*   **RF-ADM-02: Gestión de Miembros:** Administración de perfiles de Adoptantes y Voluntarios, incluyendo la aprobación de nuevas incorporaciones al equipo.
*   **RF-ADM-03: Control de Adopciones:** Revisión, aprobación o rechazo de solicitudes. El sistema debe impedir la adopción duplicada de un mismo animal.
*   **RF-ADM-04: Generación de Informes y Exportación de Datos:** El sistema soporta un motor de exportación dual. Por un lado, genera automáticamente documentación legal en **PDF** (Contrato de Adopción y Certificado de Ingreso) con datos autocompletados mediante el motor **Flying Saucer**. Por otro, todos los listados maestros del Administrador (animales, adopciones, adoptantes, voluntarios, solicitudes, donaciones, historiales y tareas) disponen de exportación a **Excel (.xlsx)** mediante la librería **Apache POI**, facilitando auditorías y análisis operativos externos.
*   **RF-ADM-05: Asignación y Trazabilidad de Tareas:** Capacidad de asignar animales y tareas específicas (limpieza, medicación, paseo) a voluntarios. El sistema debe mantener un **historial de auditoría inmutable** (solo accesible para el Administrador) que registre exactamente cuándo y qué usuario modificó el estado de una tarea, garantizando la trazabilidad de los cuidados.
*   **RF-ADM-06: Panel de Estadísticas:** Visualización de métricas clave como número de adopciones mensuales, stock de animales y volumen de donaciones.
*   **RF-ADM-07: Gestión de Logros:** Configuración de logros y recompensas dentro del sistema de gamificación.
*   **RF-ADM-08: Exportación de Reportes:** Capacidad de exportar a PDF y Excel los listados de voluntarios, animales, adopciones, donaciones y tareas.
*   **RF-ADM-09: Calendario Interactivo:** Visualización integral de las tareas y de la disponibilidad de los voluntarios en un formato de calendario dinámico (FullCalendar).

---

#### 3. Actor: Voluntario (Operativa Diaria)
Enfocado en el cuidado directo y seguimiento.

*   **RF-VOL-01: Gestión de Tareas Propias:** Visualización de la lista de tareas asignadas y capacidad de marcar el progreso (Pendiente, En curso, Finalizada). Cualquier cambio de estado quedará registrado en su historial, lo que alimentará automáticamente su progreso en el sistema de medallas.
*   **RF-VOL-02: Registro de Intervenciones Médicas:** Añadir entradas al historial médico de un animal (vacunas, incidencias, observaciones de comportamiento).
*   **RF-VOL-03: Perfil Dual:** Si el voluntario también es adoptante, el sistema debe permitir la conmutación entre el Dashboard de gestión y el Panel personal sin cerrar sesión.
*   **RF-VOL-04: Gestión de Disponibilidad y Turnos:** Los voluntarios pueden definir de forma interactiva (en modo calendario) los días y turnos en los que están disponibles para asistir al refugio.

---

#### 4. Actor: Visitante (Público Anónimo)
Cualquier usuario que accede a la web sin autenticarse.

*   **RF-VIS-01: Exploración del Catálogo:** Consulta de animales disponibles con filtros básicos (especie, edad).
*   **RF-VIS-02: Visualización de Ficha Pública:** Acceso a la información básica de un animal para fomentar el interés.
*   **RF-VIS-03: Registro de Cuenta:** Capacidad de crear una cuenta para pasar al rol de Adoptante o solicitar ser Voluntario.

---

#### 5. Actor: Adoptante (Usuario Registrado)
Usuario autenticado interesado en colaborar o adoptar.

*   **RF-ADO-01: Tramitación de Solicitudes:** Envío de formularios de solicitud de adopción para un animal concreto tras haber iniciado sesión.
*   **RF-ADO-02: Seguimiento de Trámites:** Consulta del estado de sus solicitudes en tiempo real y recepción de notificaciones de cambio de estado.
*   **RF-ADO-03: Gestión de Perfil Personal:** Actualización de datos de contacto y preferencias de adopción.
*   **RF-ADO-04: Pasarela de Donaciones:** Realización de aportaciones económicas y consulta del histórico de donaciones realizadas.
*   **RF-ADO-05: Acceso a Documentación:** Descarga de contratos de adopción una vez que el proceso ha sido finalizado y aprobado por el administrador.
*   **RF-ADO-06: Gestión de Favoritos:** Capacidad de marcar animales como favoritos para un acceso rápido y seguimiento.
*   **RF-ADO-07: Sistema de Logros y Recompensas:** Visualización de logros desbloqueados y progreso de métricas personales (donaciones, tareas) mediante el panel de gamificación.
*   **RF-ADO-08: Preferencias de Adopción:** Configuración de un perfil de preferencias (tamaño, nivel de energía, edad, especie) para agilizar las recomendaciones de animales compatibles.

---

[Volver al Índice de Documentación](/README.md)
