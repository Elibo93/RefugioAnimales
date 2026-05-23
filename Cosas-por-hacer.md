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
- [✅] **Navegación:** Agrupar en secciones:
  - **Miembros del refugio:** Usuarios, Adoptantes, Voluntarios (con submenú para Tareas).
  - **Adopciones:** Solicitudes y Adopciones Procesadas.
  - **Gestión:** Animales e Historiales Médicos.

- [✅] **Listados:**
  - ✅ Cambiar estética de animales a formato lista simple con miniatura.
  - ✅ Botón de "Asignar Tareas" en lista de voluntarios.
  - ✅Botón de "Ver Historial Médico" en lista de animales.
  - ✅-Hacer clickables los nombres en las listas para ir al perfil. 

### 2) Frontend: Perfiles Detallados
- [✅] **Perfil de Persona:** Página exclusiva que muestre info de adoptante y/o voluntario, animales adoptados y seguimiento.
- [✅] **Perfil de Animal:** Página exclusiva con datos completos, historial médico, estadísticas de vistas e info del adoptante.

### 3) Frontend: Panel del Adoptante
- [✅] **"Mis Adoptados":** Lista de sus animales. Si no hay ninguno, mostrar mensaje: *"Vaya, parece que aún no te has hecho con ninguno de nuestros amiguitos"* + botón "Adopta ahora".
- [✅] **Notificaciones:** Estados de solicitudes (aprobada/rechazada) y sugerencias de animales.
- [✅] **Encuesta:** Formulario de preferencias para activar recomendaciones inteligentes.

### 4) Frontend/Backend: Panel del Voluntario y Tareas
- [✅] **"Mis Tareas":** Lista de tareas asignadas con estados: **Pendiente, Propuesta, Aceptada, En progreso, Finalizada, Rechazada, Cancelada**.
- [✅] **Gestión de Propuestas:** El voluntario debe poder aceptar o rechazar tareas enviadas por el admin.
- [✅] **Notificaciones:** Tareas nuevas y recordatorios de fechas de vencimiento.

### 5) Backend: Lógica de Adopción y Roles
- [✅] **Dualidad de Roles:** Permitir que un usuario sea Adoptante y Voluntario simultáneamente.
- [✅] **Validaciones:**
  -[✅] Impedir adopciones duplicadas del mismo animal.
  -[✅] Acceso a funciones de voluntario solo tras aprobación del admin.
- [✅] **Contratos:** Sistema automático de generación y asociación de contratos legales al formalizar la adopción.

### 6) Backend: Notificaciones y Administración
- [✅] **Buzón Admin:** Notificar nuevas solicitudes de voluntariado, adopción y donaciones.
- [✅] **Lógica de Histórico:** Guardar quién cambió el estado de una tarea y cuándo.

### 7) Sistema de Logros (Gamificación)
- [✅] **Donantes:** Insignias según el importe total donado.
- [✅] **Voluntarios:** Méritos por antigüedad y cantidad de tareas completadas.

### 8) Gestión Multimedia y Archivos
- [✅] **Imágenes:** Subida de fotos desde el equipo local y almacenamiento (BD o filesystem).
- [✅] **Exportaciones:** Arreglar PDF de todas las listas. Implementar exportación de contratos. Añadir Excel si es posible.

### 9) Donaciones y Seguridad
- [✅] **Pagos:** Simulación de pasarela de pago (modo prueba).
- [✅] **OAuth:** Registro/Login con Google y Apple (Opcional).

### 10) Calidad y DevOps
- [✅] **i18n:** Traducir toda la app (ES/EN) usando archivos de propiedades.
- [ ] **Docker:** Unificar toda la infraestructura (Gateway, Auth, Backend, Frontend, DB) en un `docker-compose.yml`.
- [ ] **Código:** Refactorizar controladores, unificar mappers, CSS en el front y archivos scripts y completar JavaDoc, -> 

    ## Análisis y Propuesta de Refactorización Integral

    Este documento detalla el análisis exhaustivo del código actual y propone un plan para refactorizar y limpiar el proyecto sin alterar sus funcionalidades, cumpliendo con el punto "Refactorizar controladores, unificar mappers, CSS en el front y archivos scripts y completar JavaDoc" de la lista de tareas.

    ### 1. Controladores del Frontend (ViewController)

    **Situación actual:** 
    Los controladores del frontend (`AnimalViewController`, `AdopcionViewController`, etc.) son excesivamente grandes ("Fat Controllers"). Contienen mucha lógica de negocio de presentación y, sobre todo, realizan llamadas directas usando `RestTemplate`. Esto hace que el código sea repetitivo (muchos `try/catch`, URLs hardcodeadas o construidas manualmente) y difícil de testear o mantener.

    **Propuesta de mejora:**
  - **Crear una capa de Servicios en el Frontend (`es.refugio.frontend.service`):** Extraer todas las llamadas de `RestTemplate` y la lógica de paginación/filtrado a clases de servicio (ej. `AnimalService`, `VoluntarioService`).
  - El controlador se limitará únicamente a inyectar el servicio, recibir los datos y pasarlos al `Model`.

    ### 2. Unificación de Mappers (Backend)

    **Situación actual:** 
    En el paquete `es.refugio.refugio.infraestructure.mapper`, existen múltiples clases estáticas (ej. `AnimalMapper`, `AdopcionMapper`) que realizan el mapeo manual entre Entidades, Objetos de Dominio y DTOs (Request/Response/Commands). Esto genera mucho código "boilerplate" propenso a errores humanos (olvidar mapear un campo nuevo).

    **Propuesta de mejora:**
  - **Migrar a MapStruct:** Es el estándar actual en Java. Nos permite definir interfaces simples y él autogenerará el código de mapeo en tiempo de compilación. Eliminará miles de líneas de código repetitivo y será mucho más seguro.

    ### 3. Arquitectura CSS (`estilos.css`)

    **Situación actual:** 
    Todo el CSS del proyecto está concentrado en un único y gigantesco archivo `estilos.css` de 77 KB. Es inmanejable, propenso a colisiones de estilos y difícil de depurar.

    **Propuesta de mejora:**
  - Modularizar el CSS utilizando `@import`. Podemos dividirlo en:
    - `variables.css` (Colores, fuentes, sombras).
    - `layout.css` (Estructura base, header, sidebar, main).
    - `components.css` (Tarjetas, botones, modales, badges).
    - `utilities.css` (Clases auxiliares de margen, padding, display).

    ### 4. Archivos Scripts (`scripts.js`)

    **Situación actual:** 
    Al igual que el CSS, hay un solo archivo `scripts.js` de más de 54 KB con lógica mezclada: manipulación del DOM, llamadas AJAX, inicialización de componentes (Select2, HTMX listeners, gráficas, etc.).

    **Propuesta de mejora:**
  - Dividir en módulos:
    - `ui.js`: Manejo del sidebar, modales, y alertas.
    - `api.js`: Utilidades para llamadas a endpoints (si se usan fuera de HTMX).
    - `forms.js`: Inicialización de Select2, validaciones de formularios.
    - `pages/`: Scripts específicos de una vista concreta (ej. `dashboard.js` para las gráficas).

    ### 5. JavaDoc

    **Situación actual:** 
    Falta documentación estructurada en las clases críticas (Casos de Uso, Controladores y Servicios).

    **Propuesta de mejora:**
  - Establecer un estándar rápido de JavaDoc para:
    - Propósito de la clase (Class-level).
    - Funcionalidad de métodos públicos complejos (`@param`, `@return`, `@throws`).

    ---

    > [!WARNING]
    > **Riesgos y Consideraciones**
    > Dado que es una refactorización profunda, se hará **por fases** para asegurar que no se rompe nada. Empezaríamos por los Mappers o los archivos CSS/JS estáticos, y luego atacaríamos los controladores gradualmente.

    > [!IMPORTANT]
    > ## User Review Required
    > Necesito tu confirmación sobre:
    > 1. ¿Estás de acuerdo con implementar **MapStruct** para los mappers en el backend? Requiere añadir una dependencia a Maven, pero te ahorrará mucho código.
    > 2. Para los scripts y el CSS, ¿prefieres dividirlo usando importaciones nativas (múltiples archivos referenciados en el HTML o mediante `@import` en CSS) para evitar configurar herramientas como Webpack/Vite?
    > 3. ¿Por qué fase prefieres que empecemos para ir comprobando que todo sigue funcionando?
 
--- 

### 11) Restaurar validaciones de DNI
- [✅] Falta la validación de DNI
- [✅] Archivos modificados para que funcione:
- [✅] AdoptanteRequest.java (no aplica, no tiene DNI)
- [✅] PublicSolicitudAdopcionRequest.java
- [✅] ConvertAdoptanteRequest.java
- [✅] ConvertirAdoptanteRequest.java (no aplica, no tiene DNI)

### 12) Implemtar Calendario de Tareas del Voluntario
- [✅] 


---

*Este documento es una guía viva para el desarrollo del TFG - Refugio de Animales.*

