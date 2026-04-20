### Requisitos no funcionales (NF) - Refugio de Animales
---

#### Requisitos No Funcionales (RNF) de Arquitectura y Mantenibilidad

##### Adherencia Estricta a la Arquitectura Hexagonal
El sistema de gestión del refugio debe **respetar de manera estricta** los principios de la Arquitectura Hexagonal:
* **Dirección de Dependencias:** Flujo siempre hacia el interior, hacia el **Dominio (Bienestar Animal)**.
* **Aislamiento de la Lógica:** La lógica de cuidado de animales, trámites de adopción y gestión de voluntarios debe estar libre de dependencias de frameworks externos.

##### Modularidad y Escalabilidad mediante Vertical Slicing
El proyecto se organiza en **Vertical Slices** para facilitar el mantenimiento y la evolución:
* **Organización por Funcionalidad:** Paquetes como `com.refugio.animales`, `com.refugio.personas`, `com.refugio.adopciones`.
* **Independencia Funcional:** Añadir un nuevo tipo de animal o proceso no debe impactar en los módulos existentes.

##### Garantía de Testabilidad (Unit Testing)
Debe ser posible ejecutar **pruebas unitarias** sobre la lógica de adopción y salud animal de manera aislada, sin requerir el arranque de Spring ni la base de datos MySQL.

---

#### Requisitos No Funcionales (RNF) de Desacoplamiento y Persistencia

##### Abstracción Total de la Persistencia (Database Agnostic)
Aunque el refugio use MySQL en producción, el sistema debe ser agnóstico al motor de base de datos. El cambio a otra tecnología (ej. PostgreSQL para búsquedas geográficas de adoptantes en el futuro) debe ser puramente de configuración.

---

#### Requisitos No Funcionales (NF) - Seguridad

* **RNF-SEG-01:** Delegar la seguridad en **Spring Security**.
* **RNF-SEG-02:** Identificación obligatoria mediante credenciales para el personal del refugio.
* **RNF-SEG-03 (RBAC):** Acceso restringido según el rol (Administrador vs Voluntario vs Público).
* **RNF-SEG-04:** Encriptación de contraseñas mediante **BCrypt**.
* **RNF-SEG-05:** Protección contra ataques comunes (CSRF) habilitada por defecto.

---
  
[Volver](/README.md)
