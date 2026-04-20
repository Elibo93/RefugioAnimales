### Plan de Pruebas y Estrategia de QA - Refugio
---

El objetivo es garantizar que la gestión de datos sensibles (historiales y contratos) sea fiable y libre de errores mediante pruebas automatizadas.

#### Metodología: Patrón AAA (Arrange, Act, Assert)
Utilizamos **JUnit 5** y **Mockito** para validar cada componente de forma aislada.

#### 1. Pruebas Unitarias (Dominio)
* **Validación de Reglas:** Asegurar que un animal no pueda ser adoptado si su estado de salud es crítico.
* **Mapeo de Datos:** Verificar que los conversores entre objetos de base de datos y objetos de negocio no pierden información del chip o contacto.

#### 2. Pruebas de Integración (Persistencia)
Utilizamos `@DataJpaTest` con **H2 Database** para verificar que:
* **CRUD Animales:** Los registros se guardan y recuperan correctamente.
* **Integridad:** No se pueden borrar animales que tengan procesos de adopción activos.
* **Restricciones:** El sistema lanza error si se intenta registrar dos veces el mismo número de chip.

#### Herramientas de Calidad
* **JUnit 5 Jupiter** para ejecución.
* **Mockito** para simular comportamientos externos.
* **AssertJ** para aserciones legibles y precisas.

---

[Volver](/README.md)
