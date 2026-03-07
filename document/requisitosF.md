### Requisitos funcionales (F) - Refugio de Animales
---

<p align="center">
  <img src="/img/rf_refugio.png" width="600">
</p>

En este apartado se describe las funciones principales que la aplicación del **Refugio de Animales** debe realizar para gestionar eficientemente sus recursos, animales y procesos de adopción. Estos requisitos definen el comportamiento esperado del sistema desde la perspectiva del usuario final.

#### Requisitos Funcionales (F) - Gestión por Roles

A continuación, se describen las funcionalidades del sistema segregadas por el perfil de usuario que interactúa con la aplicación, definiendo el alcance y los permisos de cada actor.

##### 1. Actor: Sistema / Común
Reglas transversales que aplican a la lógica interna de la aplicación.
* **RF-SYS-01:** El sistema validará automáticamente los datos de entrada (campos obligatorios, formatos de fecha, emails válidos y duplicados).
* **RF-SYS-02:** El sistema dispondrá de un buscador transversal para localizar entidades (animales por chip/nombre, voluntarios, solicitudes).

##### 2. Actor: Administrador
Usuario con privilegios totales para la gestión estratégica y técnica del refugio.
* **RF-ADM-01:** Acceso completo a funciones CRUD (Crear, Leer, Actualizar, Borrar) de Animales.
* **RF-ADM-02:** Gestión integral de Voluntarios (altas, bajas y asignación de turnos).
* **RF-ADM-03:** Supervisión y validación final de los procesos de Adopción.
* **RF-ADM-04:** Capacidad para configurar parámetros globales del sistema (tipos de estancia, categorías de animales).

##### 3. Actor: Voluntario
Usuario operativo encargado del cuidado diario y seguimiento de los animales.
* **RF-VOL-01:** Consultar la ficha técnica y médica de los animales asignados a su zona.
* **RF-VOL-02:** Registrar tareas diarias (limpieza, paseos, alimentación) y observaciones de comportamiento.
* **RF-VOL-03:** Actualizar el estado de salud básico o incidencias médicas de un animal.

##### 4. Actor: Adoptante / Público
Usuario externo interesado en la adopción responsable.
* **RF-PUB-01:** Consultar el catálogo de animales disponibles para adopción con filtros (especie, edad, tamaño).
* **RF-PUB-02:** Iniciar una solicitud de adopción para un animal específico.
* **RF-PUB-03:** Consultar el estado de sus trámites o solicitudes enviadas.

--- 

[Volver](/README.md)
