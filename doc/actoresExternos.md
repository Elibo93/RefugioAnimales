### Casos de uso – F (Actores externos)
---
<p align="center">
  <img src="/img/actores_refugio.png" width="550">
</p>

Este apartado define cómo interactúan los diferentes perfiles con el sistema del refugio. La lógica del sistema se apoya en las entidades principales del modelo de datos, adaptando la experiencia según el tipo de usuario.

---

#### Adoptante (Público)
Usuario externo registrado en el sistema con rol de adoptante.

* Consultar el **catálogo de animales** (Entidad: Animal).
* Filtrar por **características** como especie, edad o estado.
* Enviar **solicitudes de adopción** (Entidad: SolicitudAdopcion).
* Consultar el estado de sus **solicitudes** (pendiente, aprobada, rechazada).
* Visualizar sus **adopciones realizadas** (Entidad: Adopcion).

---

#### Voluntario (Operativo)
Usuario interno con rol de voluntario encargado del cuidado de los animales.

* Consultar la **lista de animales** del refugio (Entidad: Animal).
* Registrar información relevante sobre el estado del animal.
* Añadir entradas al **historial médico** (Entidad: HistorialMedico).
* Colaborar en el seguimiento del estado general de los animales.

---

#### Administración (Gestión)
Usuario con rol administrador, responsable de la gestión global del sistema.

* Gestión completa de **animales** (altas, bajas, modificaciones) (Entidad: Animal).
* Gestión de **usuarios** (Entidad: Usuario).
* Validación de **adoptantes** (Entidad: Adoptante).
* Gestión y revisión de **solicitudes de adopción** (Entidad: SolicitudAdopcion).
* Formalización de **adopciones** (Entidad: Adopcion).
* Control del **historial médico** de los animales (Entidad: HistorialMedico).
* Registro y seguimiento de **donaciones** (Entidad: Donacion).

---

Todos los accesos están protegidos mediante **Spring Security**, garantizando el control de acceso basado en roles (ADMIN, VOLUNTARIO, ADOPTANTE). De este modo, se asegura que cada usuario solo pueda interactuar con la información correspondiente a su perfil.

---

[Volver](/README.md)