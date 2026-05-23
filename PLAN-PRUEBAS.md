# 🐾 Plan de Pruebas Funcionales — Refugio de Animales (TFG)

> **Versión:** 1.0 · **Autores:** Elisabeth · Diego  
> **Entorno:** `http://localhost:8080` (API Gateway) — todos los servicios deben estar activos.  
> **Accesos requeridos:** cuenta `ADMIN`, cuenta `PUBLICO/VOLUNTARIO` y sesión anónima.

---

## Índice de módulos

| # | Módulo | Rol requerido |
|---|--------|---------------|
| 1 | [Autenticación y Registro](#1-autenticación-y-registro) | Anónimo / Todos |
| 2 | [Home — Dashboard](#2-home--dashboard) | Todos |
| 3 | [Catálogo de Animales (Público)](#3-catálogo-de-animales-público) | Anónimo / Todos |
| 4 | [Gestión de Animales (Admin)](#4-gestión-de-animales-admin) | ADMIN |
| 5 | [Solicitudes de Adopción](#5-solicitudes-de-adopción) | Todos / ADMIN |
| 6 | [Gestión de Adoptantes](#6-gestión-de-adoptantes) | ADMIN |
| 7 | [Gestión de Adopciones](#7-gestión-de-adopciones) | ADMIN |
| 8 | [Historial Médico](#8-historial-médico) | ADMIN |
| 9 | [Voluntarios](#9-voluntarios) | Todos / ADMIN |
| 10 | [Gestión de Usuarios (Personas)](#10-gestión-de-usuarios-personas) | ADMIN |
| 11 | [Tareas de Voluntariado](#11-tareas-de-voluntariado) | ADMIN / VOLUNTARIO |
| 12 | [Donaciones](#12-donaciones) | Todos / ADMIN |
| 13 | [Notificaciones](#13-notificaciones) | Autenticado |
| 14 | [Preferencias de Adopción y Matching](#14-preferencias-de-adopción-y-matching) | Autenticado |
| 15 | [Gamificación y Logros](#15-gamificación-y-logros) | ADMIN / VOLUNTARIO |
| 16 | [Exportaciones (PDF / Excel)](#16-exportaciones-pdf--excel) | ADMIN |
| 17 | [Sistema y Arquitectura](#17-sistema-y-arquitectura) | Demo técnica |

---

## 1. Autenticación y Registro

> **URL base:** `http://localhost:8080`

### 1.1 Registro de nueva cuenta pública
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/registro` | Se muestra el formulario de registro |
| 2 | Rellenar nombre, email y contraseña | Campos aceptan la entrada |
| 3 | Enviar el formulario | Redirección al Home. Sesión iniciada automáticamente como `ROLE_PUBLICO` |
| 4 | Verificar toast de confirmación | Aparece mensaje de bienvenida verde |

### 1.2 Inicio de sesión
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/login` | Se muestra el formulario de login |
| 2 | Introducir credenciales válidas | Redirección al home con sesión activa |
| 3 | Introducir credenciales inválidas | Permanece en `/login` con mensaje de error |

### 1.3 Cierre de sesión
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Pulsar el botón de cerrar sesión en la barra superior | Sesión destruida. Redirección a `/login` |

---

## 2. Home — Dashboard

> **URL:** `/web/home`

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Navegar al home | Se cargan las estadísticas globales: nº animales, voluntarios y adopciones |
| 2 | Verificar el bloque "Top 3 favoritos" | Se muestran los 3 animales con más visitas |
| 3 | Verificar el bloque de impacto / donaciones | Se muestra el resumen de donaciones |
| 4 | Verificar acceso diferenciado | Los botones de Admin solo son visibles con rol `ADMIN` |

---

## 3. Catálogo de Animales (Público)

> **URL:** `/web/animales`

### 3.1 Listado y navegación
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Acceder a `/web/animales` | Se muestra el catálogo paginado de animales |
| 2 | Navegar entre páginas (paginación inferior) | Cambia la página sin recargar |
| 3 | Abrir el modal de detalle de un animal (clic en tarjeta) | Modal animado con foto, descripción y estado |
| 4 | Acceder a la página de detalle completo | `/web/animales/{id}` con historial médico e información de adopción |

### 3.2 Filtros combinados
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Filtrar por **especie** (p. ej. "Perro") | Solo se muestran perros |
| 2 | Añadir filtro de **sexo** | Combinación de ambos filtros |
| 3 | Añadir filtro de **edad** (múltiple: Cachorro + Joven) | Solo se muestran las edades seleccionadas |
| 4 | Activar filtro **Urgente** | Solo animales marcados como urgentes |
| 5 | Filtrar por **estado** (Disponible, Reservado…) | Resultados filtrados por estado |
| 6 | Usar la **búsqueda por nombre/chip** (campo `q`) | Resultados dinámicos por nombre o chip ID |
| 7 | Pulsar **Mis Favoritos** | Solo se muestran animales marcados como favoritos |
| 8 | Limpiar todos los filtros | Vuelve a mostrar todos los animales |

### 3.3 Sistema de Favoritos
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Pulsar el ❤️ en una tarjeta de animal (usuario autenticado) | El corazón se llena y el contador aumenta |
| 2 | Pulsar de nuevo | El corazón se vacía (toggle) |
| 3 | Intentar con sesión anónima | El botón no aparece o no aplica cambios |

---

## 4. Gestión de Animales (Admin)

> **Rol requerido:** ADMIN · **URL base:** `/web/animales`

### 4.1 Alta de animal
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/animales/nuevo` | Formulario de creación vacío |
| 2 | Rellenar todos los campos (nombre, especie, raza, chip, estado, foto…) | Formulario acepta todos los datos |
| 3 | Subir imagen desde el ordenador | La foto se previsualiza antes de guardar |
| 4 | Guardar | Toast verde de éxito. El animal aparece en la lista |

### 4.2 Edición de animal
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | En la lista, pulsar "Editar" en un animal | Formulario precargado con los datos actuales |
| 2 | Modificar algún campo y guardar | Toast de éxito. Los cambios se reflejan en la lista |

### 4.3 Eliminación de animal
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | En la lista, pulsar "Borrar" en un animal | Modal de confirmación (si aplica) |
| 2 | Confirmar eliminación | El animal desaparece de la lista sin recargar la página |

---

## 5. Solicitudes de Adopción

> **URL base:** `/web/solicitudes`

### 5.1 Flujo Público — Usuario anónimo / registrado sin perfil
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/solicitudes/publico/opciones` | Se muestran las opciones: "Registrarse", "Ya soy adoptante", "Solicitud directa" |
| 2 | Elegir **"Registro y Adopción"** | Formulario para crear cuenta y solicitar a la vez |
| 3 | Rellenar y enviar | Toast de confirmación. Solicitud creada en estado PENDIENTE |

### 5.2 Flujo Público — Usuario autenticado
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Desde el detalle de un animal, pulsar "Solicitar Adopción" | Redirige al formulario de solicitud directa |
| 2 | Rellenar el formulario | Formulario con datos prefill del usuario |
| 3 | Enviar | Solicitud creada. Vista de confirmación `/web/solicitudes/publico/solicitud-creada` |

### 5.3 Gestión de Solicitudes (Admin)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/solicitudes` | Lista completa de todas las solicitudes con paginación |
| 2 | Ver el detalle de una solicitud | `/web/solicitudes/{id}/detalle` con toda la información |
| 3 | **Aprobar** una solicitud en PENDIENTE | Toast de éxito. Estado cambia a APROBADA. El animal pasa a RESERVADO. Se genera la Adopción |
| 4 | **Rechazar** una solicitud | Toast informativo. Estado cambia a RECHAZADA |
| 5 | Poner una solicitud **En Revisión** | Estado cambia a EN_REVISION |
| 6 | Filtrar solicitudes por estado | Lista filtrada correctamente |

### 5.4 Mis Adoptados (usuario autenticado)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/solicitudes/mis-adoptados` | Lista de animales adoptados por el usuario actual |

---

## 6. Gestión de Adoptantes

> **Rol requerido:** ADMIN · **URL base:** `/web/adoptantes`

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Listar adoptantes en `/web/adoptantes` | Lista paginada con búsqueda |
| 2 | Ver formulario de nuevo adoptante `/web/adoptantes/nuevo` | Formulario vacío |
| 3 | Editar un adoptante `/web/adoptantes/{id}/editar` | Formulario precargado |
| 4 | **Aprobar** un adoptante (estado PENDIENTE → APROBADO) | Toast de confirmación |
| 5 | **Rechazar** un adoptante | Toast y estado RECHAZADO |
| 6 | Eliminar un adoptante | Desaparece de la lista |

---

## 7. Gestión de Adopciones

> **Rol requerido:** ADMIN · **URL base:** `/web/adopciones`

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Listar adopciones | Lista paginada con estados |
| 2 | Editar una adopción | Cambiar estado del contrato |
| 3 | **Generar contrato PDF** `/web/adopciones/{id}/contrato` | Descarga el PDF del contrato de adopción |
| 4 | Eliminar una adopción | Desaparece de la lista |

---

## 8. Historial Médico

> **Rol requerido:** ADMIN · **URL base:** `/web/historiales`

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Desde el detalle de un animal, ver la sección "Historial Médico" | Se listan los registros médicos del animal |
| 2 | Ir a `/web/historiales/nuevo?animalId={id}` | Formulario de nuevo registro médico |
| 3 | Rellenar (diagnóstico, tratamiento, fecha) y guardar | Registro aparece en el detalle del animal |
| 4 | Editar un historial | Formulario precargado |
| 5 | Eliminar un historial | Desaparece de la lista |

---

## 9. Voluntarios

> **URL base:** `/web/voluntarios`

### 9.1 Registro como voluntario (cualquier usuario autenticado)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/voluntarios/nuevo` | Formulario de solicitud de voluntariado |
| 2 | Si ya tiene perfil legal, los datos se precargan automáticamente | Los campos de nombre, DNI, etc. aparecen rellenos |
| 3 | Si no tiene perfil, rellenar datos personales + disponibilidad + especialidad | Formulario completo |
| 4 | Enviar solicitud | Toast de confirmación. Solicitud en estado PENDIENTE. Admin recibe notificación |

### 9.2 Gestión de solicitudes de voluntarios (Admin)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/voluntarios/pendientes` | Lista de solicitudes pendientes de aprobación |
| 2 | **Aprobar** un voluntario | Toast de éxito. El voluntario pasa a ACTIVO. Usuario recibe notificación |
| 3 | **Rechazar** un voluntario | Toast informativo. Solicitud rechazada |

### 9.3 Listado y gestión de voluntarios activos (Admin)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Listar en `/web/voluntarios` con búsqueda | Lista paginada con buscador |
| 2 | Ver disponibilidad de un voluntario (modal) | Modal con el calendario/slots de disponibilidad |
| 3 | Editar un voluntario | Formulario con datos actuales |
| 4 | Eliminar un voluntario | Desaparece de la lista |

---

## 10. Gestión de Usuarios (Personas)

> **Rol requerido:** ADMIN · **URL base:** `/web/personas`

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Listar en `/web/personas` | Lista de todos los usuarios del sistema |
| 2 | Ver el detalle de una persona `/web/personas/{id}` | Ficha completa: datos personales, rol, voluntariado, adopciones |
| 3 | Editar una persona | Formulario de edición de datos |
| 4 | Crear nueva persona desde admin `/web/personas/nuevo` | Formulario de alta de usuario |
| 5 | Eliminar una persona | Desaparece de la lista |

---

## 11. Tareas de Voluntariado

> **Rol requerido:** ADMIN (gestión) / VOLUNTARIO (consulta) · **URL base:** `/web/tareas`

### 11.1 Ciclo de vida de una tarea (Admin)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/tareas` | Lista de todas las tareas con estados y fechas |
| 2 | Crear nueva tarea `/web/tareas/nueva` | Formulario con descripción, fecha límite, asignación de voluntarios |
| 3 | Asignar voluntarios mediante el buscador | Se pueden seleccionar múltiples voluntarios |
| 4 | Guardar | Toast de éxito. Tarea aparece en la lista |
| 5 | Editar la tarea | Cambiar estado (PENDIENTE → ACEPTADA → EN_PROCESO → COMPLETADA) |
| 6 | Ver **historial de cambios** `/web/tareas/{id}/historial` | Línea de tiempo con todos los cambios de estado |
| 7 | Eliminar tarea | Desaparece de la lista |

### 11.2 Sistema de recordatorios automáticos
| Paso | Acción | Resultado esperado (indirecto) |
|------|--------|-------------------------------|
| 1 | Crear tarea con fecha límite dentro de las próximas 24h | El scheduler la detecta en el siguiente ciclo (1h) |
| 2 | Esperar o forzar ejecución | El voluntario asignado recibe una notificación URGENTE |

---

## 12. Donaciones

> **URL base:** `/web/donaciones`

### 12.1 Realizar una donación (público)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/donaciones/nueva` | Formulario de donación con importe, concepto y datos personales |
| 2 | Rellenar e ir a la pasarela `/web/donaciones/pasarela` | Pantalla de pago simulada |
| 3 | Confirmar el pago | Redirección a la página de agradecimiento `/web/donaciones/gracias` |
| 4 | Verificar que el donante recibe notificación | Toast/notificación de confirmación |

### 12.2 Página de Impacto (pública)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Acceder a `/web/donaciones/impacto` | Datos visuales del impacto de las donaciones |

### 12.3 Gestión de donaciones (Admin)
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Ir a `/web/donaciones` | Lista de todas las donaciones |
| 2 | Editar una donación | Cambiar estado o datos |
| 3 | **Crear/Editar Objetivo de Donación** `/web/donaciones/objetivo-donacion` | Formulario para el objetivo |
| 4 | Eliminar una donación | Desaparece de la lista |

---

## 13. Notificaciones

> **Requiere:** usuario autenticado · **URL base:** `/web/notificaciones`

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Observar el icono de campana en la barra superior | Muestra el contador de notificaciones no leídas |
| 2 | Ir a `/web/notificaciones` | Lista de todas las notificaciones del usuario |
| 3 | Pulsar sobre una notificación | Se marca como leída. El contador disminuye |
| 4 | Verificar que el tipo se muestra correctamente | Icono/color diferente para MATCH, ADOPCION, SISTEMA, URGENTE, LOGRO |

---

## 14. Preferencias de Adopción y Matching

> **Requiere:** usuario autenticado

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Acceder a `/web/preferencias/encuesta` | Formulario multi-paso de preferencias |
| 2 | Seleccionar especies, tamaños, sexo y edad máxima preferida | Los filtros se guardan al usuario |
| 3 | Activar notificaciones de matching | El usuario recibirá alertas cuando llegue un animal compatible |
| 4 | **Desde el panel Admin:** crear un nuevo animal que cumpla las preferencias | El sistema ejecuta el motor de matching |
| 5 | Verificar que el usuario recibe una notificación de tipo "MATCH" | Aparece en `/web/notificaciones` y en el contador de campana |

---

## 15. Gamificación y Logros

> **URL:** `/web/gamificacion/perfil` (para el propio usuario)

| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Completar una tarea como voluntario (cambiar estado a COMPLETADA) | El sistema actualiza las métricas del voluntario |
| 2 | Ir a `/web/personas/{id}` del voluntario | Se puede ver el progreso de logros |
| 3 | Realizar varias donaciones hasta alcanzar el umbral de un logro | El motor de gamificación detecta el hito |
| 4 | Verificar que el usuario recibe notificación de tipo "LOGRO_DESBLOQUEADO" | Toast especial y notificación en el panel |
| 5 | Ver el perfil de gamificación | Muestra los logros obtenidos con insignias |

---

## 16. Exportaciones (PDF / Excel)

> **Rol requerido:** ADMIN

| Módulo | Exportar PDF | Exportar Excel |
|--------|-------------|----------------|
| Animales | `/web/animales/pdf` ↓ Descarga `animales.pdf` | `/web/animales/excel` ↓ Descarga `animales.xlsx` |
| Voluntarios | `/web/voluntarios/pdf` | `/web/voluntarios/excel` |
| Adoptantes | `/web/adoptantes/pdf` | `/web/adoptantes/excel` |
| Adopciones | `/web/adopciones/pdf` | `/web/adopciones/excel` |
| Solicitudes | `/web/solicitudes/pdf` | `/web/solicitudes/excel` |
| Donaciones | `/web/donaciones/pdf` | `/web/donaciones/excel` |
| Historiales | `/web/historiales/pdf` | `/web/historiales/excel` |
| Tareas | `/web/tareas/pdf` | `/web/tareas/excel` |
| Personas | `/web/personas/pdf` | `/web/personas/excel` |
| **Contrato de Adopción** | `/web/adopciones/{id}/contrato` — PDF específico de adopción | — |

---

## 17. Sistema y Arquitectura

> Esta sección es para demostración **técnica** durante la presentación del TFG.

### 17.1 Microservicios activos (puertos por defecto)
| Servicio | URL | Descripción |
|----------|-----|-------------|
| Eureka Server | `http://localhost:8761` | Registro de servicios — muestra todos los microservicios registrados |
| API Gateway | `http://localhost:8080` | Punto de entrada único |
| refugio-backend | `http://localhost:8082` | API REST principal (Swagger: `/swagger-ui.html`) |
| refugio-auth | `http://localhost:8081` | Servicio de autenticación |
| refugio-frontend | `http://localhost:8083` | Interfaz web Thymeleaf |

### 17.2 Swagger / OpenAPI
| Paso | Acción | Resultado esperado |
|------|--------|--------------------|
| 1 | Acceder a `http://localhost:8082/swagger-ui.html` | Documentación interactiva de todos los endpoints REST |
| 2 | Probar un endpoint desde Swagger (p. ej. GET `/v1/animales`) | Respuesta JSON paginada |

### 17.3 Arquitectura demostrable (sin abrir el navegador)
- **Patrón de diseño:** Domain-Driven Design (DDD) con separación en capas: Dominio → Aplicación → Infraestructura.
- **Eventos de dominio:** `TareaStatusChangedEvent`, `DonacionCompletedEvent` — asíncronos con `@Async` + `@EventListener`.
- **Gamificación:** Motor `LogroEngine` + Listeners que actualizan métricas en tiempo real.
- **Matching:** `MatchingService` que cruza preferencias de adoptantes con nuevos animales.
- **Internacionalización (i18n):** Textos de mensajes en `messages_es.properties` y `messages_en.properties`.
- **Seguridad:** JWT emitido por `refugio-auth`, validado en el gateway y propagado al frontend.
- **Exportaciones:** PDF con Flying Saucer (iText) + Excel con Apache POI.
- **HTMX:** Interacciones parciales sin recargar la página (filtros, modales, paginación).

---

## ✅ Checklist de pruebas rápidas (resumen para el día de la presentación)

```
[ ] 1. Registro de nueva cuenta → Login → Logout
[ ] 2. Dashboard: estadísticas y animales top
[ ] 3. Catálogo: filtros múltiples + favoritos + modal de detalle
[ ] 4. Admin: crear animal con foto → editar → eliminar
[ ] 5. Solicitud de adopción pública (flujo completo)
[ ] 6. Admin: aprobar solicitud → verificar adopción generada
[ ] 7. Admin: gestión de voluntarios pendientes (aprobar / rechazar)
[ ] 8. Admin: crear tarea y asignar voluntario → completar → ver historial
[ ] 9. Donación con pasarela simulada → página de agradecimiento
[ ] 10. Notificaciones: verificar campana + marcar como leída
[ ] 11. Preferencias de adopción → crear animal compatible → verificar MATCH
[ ] 12. Logro desbloqueado (completar varias tareas) → notificación LOGRO
[ ] 13. Exportar un PDF y un Excel de animales
[ ] 14. Mostrar Eureka (servicios registrados) y Swagger (API documentada)
```

---

*Documento generado automáticamente para el TFG — Refugio de Animales.*  
*Última actualización: Mayo 2026.*
