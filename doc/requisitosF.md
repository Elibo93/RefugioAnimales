### Requisitos funcionales (F) - Refugio de Animales
---

<p align="center">
  <img src="/img/rf_refugio.png" width="600">
</p>

En este apartado se describen las funcionalidades principales que la aplicación del **Refugio de Animales** debe ofrecer para gestionar de forma eficiente los animales, usuarios y procesos de adopción. Estos requisitos definen el comportamiento esperado del sistema desde la perspectiva de los diferentes tipos de usuario.

#### Requisitos Funcionales (F) - Gestión por Roles

A continuación, se detallan las funcionalidades del sistema organizadas según el perfil de usuario, estableciendo claramente el alcance y los permisos asociados a cada actor.

---

##### 1. Actor: Sistema / Común
Reglas generales aplicables a toda la aplicación.

* **RF-SYS-01:** El sistema validará automáticamente los datos de entrada, incluyendo campos obligatorios, formatos (email, fechas) y control de duplicados.
* **RF-SYS-02:** El sistema dispondrá de un buscador global para localizar entidades como animales, usuarios y solicitudes de adopción.
* **RF-SYS-03:** El sistema garantizará la integridad de los datos en las relaciones entre entidades (por ejemplo, no permitir una adopción sin solicitud previa válida).

---

##### 2. Actor: Administrador
Usuario con privilegios completos para la gestión del sistema.

* **RF-ADM-01:** Gestionar los animales del refugio mediante operaciones CRUD (Entidad: Animal).
* **RF-ADM-02:** Gestionar los usuarios del sistema, incluyendo voluntarios y adoptantes (Entidad: Usuario).
* **RF-ADM-03:** Validar o rechazar adoptantes para permitir procesos de adopción (Entidad: Adoptante).
* **RF-ADM-04:** Supervisar y gestionar las solicitudes de adopción, incluyendo su aprobación o rechazo (Entidad: SolicitudAdopcion).
* **RF-ADM-05:** Formalizar adopciones a partir de solicitudes previamente aprobadas (Entidad: Adopcion).
* **RF-ADM-06:** Gestionar el historial médico de los animales (Entidad: HistorialMedico).
* **RF-ADM-07:** Registrar y consultar donaciones realizadas al refugio (Entidad: Donacion).

---

##### 3. Actor: Voluntario
Usuario encargado del cuidado y seguimiento de los animales.

* **RF-VOL-01:** Consultar la información completa de los animales del refugio (Entidad: Animal).
* **RF-VOL-02:** Registrar observaciones sobre el estado y comportamiento de los animales.
* **RF-VOL-03:** Registrar incidencias o actualizaciones en el historial médico de un animal (Entidad: HistorialMedico).

---

##### 4. Actor: Adoptante / Público
Usuario externo interesado en la adopción.

* **RF-PUB-01:** Consultar el catálogo de animales disponibles para adopción con filtros por características (Entidad: Animal).
* **RF-PUB-02:** Registrar una solicitud de adopción para un animal específico (Entidad: SolicitudAdopcion).
* **RF-PUB-03:** Consultar el estado de sus solicitudes de adopción (Entidad: SolicitudAdopcion).
* **RF-PUB-04:** Consultar el historial de adopciones realizadas (Entidad: Adopcion).

---

[Volver](/README.md)