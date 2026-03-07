### Casos de uso – F (Actores externos)
---
<p align="center">
  <img src="/img/actores_refugio.png" width="550">
</p>

Este apartado define cómo interactúan los diferentes perfiles con el sistema del refugio. Aunque la lógica es modular, la experiencia se personaliza según el actor.

---

#### Adoptante (Público)
Usuario externo que busca integrar un nuevo miembro en su familia.
* Consultar el **catálogo de animales**.
* Filtrar por **especie/edad/necesidades**.
* Enviar **solicitudes de adopción**.
* Seguimiento de sus **trámites activos**.

---

#### Voluntario (Operativo)
Usuario interno encargado del día a día de los animales.
* Ver la **lista de animales asignados**.
* Registrar **tareas de cuidado** (paseos, alimentación).
* Notificar **incidencias de salud** o comportamiento.
* Consultar **calendario de turnos**.

---

#### Administración (Gestión)
Usuario con visión global y capacidad de decisión.
* Gestión de **altas y bajas** de animales y voluntarios.
* Validación y firma de **contratos de adopción**.
* Acceso a **estadísticas de ocupación** y éxito de adopciones.
* Gestión de la **base de datos de personas** vinculadas.

---

Todos los accesos están protegidos por **Spring Security**, asegurando que un adoptante no pueda acceder a datos sensibles de voluntarios ni a historiales médicos restringidos.

---

[Volver](/README.md)
