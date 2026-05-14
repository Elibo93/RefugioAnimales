### Interfaz de Usuario (UI) y Experiencia Visual - Refugio
---

La interfaz del sistema de gestión del refugio se ha diseñado para ser **funcional, profesional y emocionalmente conectiva**. Siguiendo el patrón **MVC** con **Thymeleaf**, las vistas actúan como adaptadores web que transforman los datos del dominio en experiencias interactivas optimizadas para cada actor.

---

#### 1. Identidad Visual y Estética
Para un proyecto de TFG, la estética es clave. Hemos definido un sistema de diseño propio:
*   **Paleta de Colores:** Uso de tonos naturales (verdes suaves, tierras y grises limpios) para transmitir calma y profesionalidad.
*   **Tipografía:** *Plus Jakarta Sans* por su modernidad y alta legibilidad en entornos de gestión.
*   **Iconografía:** Set de iconos *Lucide* para una navegación intuitiva y minimalista.
*   **Componentes Premium:** Uso de sombras suaves (*box-shadows*), bordes redondeados y micro-interacciones (hovers) para una sensación de software de alta calidad.

---

#### 2. Mapa de Vistas por Módulos

##### 2.1. Módulo de Administración (Control Total)
Herramientas de gestión estratégica para el personal directivo del refugio.

| ID Vista | Nombre | Descripción |
| :--- | :--- | :--- |
| **V-ADM-01** | **Dashboard General** | Panel con estadísticas, alertas médicas y solicitudes urgentes. |
| **V-ANI-01** | **Inventario Vivo** | Listado maestro de animales con gestión CRUD completa. |
| **V-ANI-02** | **Historial Médico** | Línea de tiempo con vacunas, intervenciones y observaciones. |
| **V-ADO-01** | **Gestión de Trámites** | Bandeja de entrada de solicitudes con flujo de aprobación/rechazo. |
| **V-PDF-01** | **Visor de Contratos** | Previsualización y descarga de documentos legales generados. |

---

##### 2.2. Módulo de Voluntariado (Operativa Diaria)
Optimizado para el uso en movilidad y el cuidado directo.

| ID Vista | Nombre | Descripción |
| :--- | :--- | :--- |
| **V-VOL-01** | **Mis Tareas** | Lista de tareas diarias (limpieza, paseo, medicación) con checkboxes. |
| **V-VOL-02** | **Reporte de Incidencias** | Formulario rápido para notificar anomalías detectadas en el patio. |

---

##### 2.3. Módulo del Adoptante (Usuario Registrado)
Panel personal para la interacción directa y seguimiento de procesos.

| ID Vista | Nombre | Descripción |
| :--- | :--- | :--- |
| **V-ADO-01** | **Panel del Adoptante** | Resumen de solicitudes activas y estado de sus trámites. |
| **V-ADO-02** | **Historial de Interés** | Listado de animales marcados como favoritos o solicitados. |
| **V-ADO-03** | **Mis Donaciones** | Registro de aportaciones económicas y descarga de certificados. |
| **V-ADO-04** | **Buzón de Mensajes** | Notificaciones del sistema sobre la evolución de sus solicitudes. |

---

##### 2.4. Módulo Público y Visitante
Enfocado en el descubrimiento y la captación de nuevos colaboradores.

| ID Vista | Nombre | Descripción |
| :--- | :--- | :--- |
| **V-PUB-01** | **Landing Page** | Presentación del refugio, misión y llamada a la acción. |
| **V-PUB-02** | **Catálogo Público** | Galería interactiva de animales disponibles con filtros rápidos. |
| **V-PUB-03** | **Ficha de Animal** | Vista detallada con historia, fotos y botón de solicitud. |
| **V-ACC-01** | **Login / Registro** | Formularios limpios con validación en tiempo real. |

---

#### 3. Interactividad Avanzada con HTMX
Para mejorar la eficiencia y evitar recargas completas, se han implementado patrones de **actualización parcial**:
*   **Inline Editing:** Los estados de las tareas se actualizan mediante peticiones HTMX que refrescan solo el componente afectado.
*   **Filtros Dinámicos:** El catálogo se filtra en tiempo real sin perder la posición del scroll, enviando fragmentos de HTML desde el servidor.
*   **Modales Reactivos:** Los formularios complejos se cargan en modales mediante HTMX, manteniendo limpia la vista principal.

---

#### 4. Diseño Responsive
La UI utiliza un sistema de rejilla flexible (CSS Grid/Flexbox) que garantiza:
- **Desktop:** Panel de administración denso con toda la información visible.
- **Tablet/Mobile:** Vistas simplificadas con botones de gran tamaño (44x44px mín.) para facilitar el registro de datos mientras se atiende a los animales.

---

[Volver al Índice de Documentación](/README.md)
