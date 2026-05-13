### Plan de Pruebas y Estrategia de QA - Refugio
---

El objetivo de este plan es garantizar que la gestión de datos críticos (salud animal, contratos legales y seguridad de usuarios) sea robusta, fiable y libre de errores. Dado que el sistema está basado en **microservicios dockerizados**, la estrategia de pruebas debe cubrir tanto la lógica aislada como la comunicación entre contenedores.

---

#### 1. Niveles de Prueba

##### 1.1. Pruebas Unitarias (Capa de Dominio y Aplicación)
Se centran en validar la lógica de negocio pura sin dependencias externas.
*   **Reglas de Negocio:** Validar que un animal no pueda pasar a estado "Adoptado" sin un microchip registrado o si su salud requiere atención urgente.
*   **Casos de Uso:** Verificar que el flujo de `SolicitarAdopcion` crea correctamente la entidad de solicitud y dispara las notificaciones necesarias.
*   **Herramientas:** JUnit 5, Mockito (para simular repositorios y servicios externos).

##### 1.2. Pruebas de Integración (Capa de Infraestructura)
Aseguran que la aplicación interactúa correctamente con las bases de datos y servicios externos.
*   **Persistencia (JPA):** Uso de **H2 Database** en memoria o **Testcontainers** para validar que las consultas personalizadas de búsqueda de animales y filtrado de voluntarios funcionan sobre un motor SQL real.
*   **Seguridad:** Validar que los filtros de Spring Security bloquean el acceso a rutas de administración si no se proporciona un JWT válido o si el rol es insuficiente.
*   **Herramientas:** `@SpringBootTest`, `@DataJpaTest`, MockMvc.

##### 1.3. Pruebas de Contrato y API (Comunicación entre Servicios)
Crucial en una arquitectura distribuida.
*   **Gateway & Eureka:** Verificar que el API Gateway redirige correctamente las peticiones a los servicios de `auth` y `backend` una vez registrados en Eureka.
*   **Validación de DTOs:** Asegurar que los cambios en el formato de datos de `refugio-common` no rompen la comunicación entre microservicios.

##### 1.4. Pruebas de Interfaz y E2E (Frontend)
Validan el flujo completo desde la perspectiva del usuario.
*   **Dinamismo HTMX:** Verificar que las actualizaciones parciales de la interfaz (ej: marcar una tarea como completada) se reflejan correctamente sin recargar la página.
*   **Generación de Documentos:** Comprobar que la exportación de **Contratos en PDF** genera archivos legibles y con los datos correctos del animal y adoptante.

---

#### 2. Entorno de Pruebas Dockerizado
Para garantizar que las pruebas sean fieles al entorno de producción:
- **Aislamiento:** Se puede levantar un entorno de pruebas idéntico al de producción usando un `docker-compose.test.yml`.
- **Limpieza de Datos:** Las pruebas de integración deben ejecutarse contra bases de datos que se reinician o limpian tras cada suite de tests para asegurar la idempotencia.

---

#### 3. Métricas y Herramientas de Calidad
*   **JUnit 5 Jupiter:** Motor de ejecución de pruebas.
*   **Mockito:** Framework de simulación para aislamiento de componentes.
*   **AssertJ:** Para aserciones legibles y semánticas.
*   **SonarLint / Checkstyle:** Para asegurar que el código sigue las convenciones de estilo y no presenta "code smells" antes de las pruebas.

---

#### 4. Ejecución del Plan
Aunque el desarrollo está en curso, se establece que:
1.  Cada nuevo **Caso de Uso** debe ir acompañado de su correspondiente test unitario.
2.  Antes de cada despliegue, se debe ejecutar la suite completa de integración en el entorno Docker para validar la conectividad.

---

[Volver al Índice de Documentación](/README.md)

