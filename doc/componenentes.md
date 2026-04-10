### Componentes - Arquitectura del Refugio
---

La arquitectura del **Sistema de Gestión del Refugio** se organiza bajo los principios de **Clean Architecture**, estructurando el sistema en capas concéntricas para garantizar la independencia tecnológica y la testabilidad.

El sistema se divide en el núcleo compartido (**Common**) y los módulos de negocio específicos (**Refugio**).

---

#### 1. Capa Common

Encapsula componentes transversales reutilizables por todos los módulos del refugio.

* **`crud-repository`:** Contratos genéricos para persistencia de animales, personas y trámites.
* **`GlobalExceptionHandler`:** Interceptor de excepciones para estandarizar errores médicos o administrativos.
* **`Identificador`:** Generación de UUIDs consistentes para todas las entidades del refugio.

---

#### 2. Capa Refugio (Vertical Slice)

Este módulo contiene la **lógica de negocio** específica para la gestión de bienestar animal. Siguiendo el patrón de *Vertical Slicing*, se subdivide en:

##### 2.1. Domain (Entidades - Reglas de negocio)
Contiene las reglas de "empresa" del refugio, sin dependencias externas.

* **`Animal`**: Gestión de la ficha, salud y disponibilidad.
* **`Persona`**: Gestión de perfiles (Voluntario, Adoptante).
* **`Adopcion`**: Lógica de trámites, estados y validaciones.
* **`Seguimiento`**: Reglas para visitas post-adopción.

##### 2.2. Application (Casos de Usos - Servicios)
Orquesta el flujo de datos y ejecuta las acciones de negocio.

* **`UseCases`:** Ejemplos: `RegistrarIngresoAnimalUseCase`, `SolicitarAdopcionUseCase`.
* **`Services` (Facade):** Coordina la persistencia y la comunicación entre dominios.

##### 2.3. Infrastructure (Interface Adapters & Frameworks)
Detalles de implementación externos (Web, DB, UI).

* **`Web Adapter`:** Controladores REST para la API de gestión.
* **`Persistence Adapter`:** Implementaciones JPA para MySQL/H2.
* **`Mapper`:** Convertidores entre objetos de Dominio y Entidades JPA.

---

#### Diagrama UML de Componentes

El diseño sigue la misma estructura modular que el proyecto base, permitiendo una transición suave hacia microservicios en el futuro si el volumen de animales gestionados lo requiere.

---

[Volver](/README.md)
