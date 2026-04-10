### Anexos - Documentación Técnica Complementaria
---

Este apartado recopila material de soporte para la implementación y auditoría del sistema de gestión del refugio.

#### Anexo A. Manual de Despliegue Rápido
Instrucciones para clonar el repositorio y ejecutar `docker compose up` para iniciar el sistema en segundos.

#### Anexo B. Diccionario de Datos
Tablas descriptivas de las entidades `Animal`, `Persona`, `Adopcion` y `Seguimiento`, definiendo tipos de datos, claves primarias y restricciones de integridad.

#### Anexo C. Especificación OpenAPI (Swagger)
Referencia completa de los endpoints de la API (REST JSON) para posibles integraciones futuras con una APP móvil para voluntarios.

#### Anexo D. Snippets de Configuración
* **`SecurityConfig.java`**: Reglas de acceso para `ROLE_ADMIN`, `ROLE_VOLUNTARIO` y `ROLE_PUBLICO`.
* **`application-prod.properties`**: Configuración de conexión al contenedor MySQL.

#### Anexo E. Glosario de Términos
Definición de conceptos como *Clean Architecture*, *Vertical Slicing*, *CRUD*, *Docker Container* y *RBAC* (Role Based Access Control).

---

[Volver](/README.md)
