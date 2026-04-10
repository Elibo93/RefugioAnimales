### Información de Versiones - Refugio de Animales
---

Para asegurar la estabilidad del sistema de gestión del refugio, se han seleccionado versiones con soporte a largo plazo (LTS).

| Tecnología | Versión | Rol en el Proyecto |
| :--- | :--- | :--- |
| **Java JDK** | **17 (LTS)** | Lenguaje base. Uso de Records para los datos de los animales. |
| **Spring Boot** | **3.x** | Framework de orquestación y servidor web embebido. |
| **Spring Data JPA**| *Managed* | Capa de persistencia para el historial de los animales. |
| **Hibernate** | **6.x** | Gestión del mapeo de la base de datos MySQL. |
| **MySQL** | **8.x** | Motor de base de datos para producción. |
| **Docker** | **20.x+** | Tecnología de contenedores para el despliegue. |

---

#### Gestión de Dependencias
Utilizamos **Maven** para asegurar que todas las librerías sean compatibles entre sí, evitando conflictos técnicos y facilitando el mantenimiento futuro del software del refugio.

---

[Volver](/README.md)
