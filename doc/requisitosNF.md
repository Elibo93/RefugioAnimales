### Requisitos No Funcionales (RNF) - Refugio de Animales
---

Los Requisitos No Funcionales definen las restricciones y atributos de calidad que el sistema debe poseer para garantizar que, además de cumplir sus funciones, sea robusto, seguro, mantenible y escalable.

---

#### 1. Arquitectura y Mantenibilidad

*   **RNF-ARQ-01: Arquitectura Hexagonal:** El sistema debe seguir estrictamente el patrón de Puertos y Adaptadores. El **Dominio** (lógica de negocio animal) no debe tener dependencias de frameworks externos ni de la base de datos.
*   **RNF-ARQ-02: Microservicios Desacoplados:** Cada servicio debe ser independiente y tener su propia responsabilidad y persistencia, comunicándose mediante interfaces claras (REST).
*   **RNF-ARQ-03: Vertical Slicing:** La organización del código dentro de los microservicios debe priorizar la agrupación por funcionalidad (ej: `animales`, `adopciones`, `personas`) para facilitar la evolución aislada.
*   **RNF-ARQ-04: Testabilidad:** El diseño debe permitir una cobertura de tests unitarios superior al 80% en la capa de dominio, ejecutable sin necesidad de arrancar el servidor o la base de datos.

---

#### 2. Rendimiento y Escalabilidad

*   **RNF-PER-01: Tiempo de Respuesta:** El sistema debe responder a las peticiones del frontend en menos de 500ms para operaciones de lectura bajo condiciones normales.
*   **RNF-PER-02: Dinamismo HTMX:** El uso de HTMX debe minimizar el tráfico de red enviando solo fragmentos de HTML necesarios, reduciendo la carga percibida por el usuario.
*   **RNF-PER-03: Escalabilidad Horizontal:** Gracias a la arquitectura de microservicios y Docker, el sistema debe permitir levantar múltiples instancias del `refugio-backend` tras el Gateway para manejar picos de carga.

---

#### 3. Seguridad y Privacidad

*   **RNF-SEG-01: Identificación y Autenticación:** Uso obligatorio de **Spring Security** con autenticación basada en **JWT (stateless)** para asegurar las peticiones entre servicios.
*   **RNF-SEG-02: Control de Acceso (RBAC):** Restricción estricta de funcionalidades según el rol: Administrador, Voluntario, Adoptante y Visitante.
*   **RNF-SEG-03: Protección de Datos:** Las contraseñas se almacenarán cifradas mediante el algoritmo **BCrypt**. La comunicación pública se realizará bajo protocolo **HTTPS**.
*   **RNF-SEG-04: Integridad de Sesión:** Protección contra ataques comunes como **CSRF** (en la capa web) y **XSS**.

---

#### 4. Portabilidad y Despliegue (Docker)

*   **RNF-POR-01: Contenerización Completa:** El sistema debe ser capaz de desplegarse íntegramente mediante **Docker y Docker Compose**, incluyendo microservicios, bases de datos y servidor de descubrimiento.
*   **RNF-POR-02: Aislamiento del Entorno:** La aplicación no debe requerir la instalación de dependencias en el host (Java, MySQL, etc.), delegando toda la gestión del entorno a Docker.
*   **RNF-POR-03: Persistencia Robusta:** El uso de volúmenes de Docker debe garantizar que los datos de las bases de datos y las imágenes de los animales persistan tras el reinicio o actualización de los contenedores.

---

#### 5. Usabilidad y Accesibilidad

*   **RNF-USA-01: Diseño Responsive:** La interfaz debe ser plenamente funcional en dispositivos móviles y tablets (uso en el patio del refugio) y en equipos de escritorio (administración).
*   **RNF-USA-02: Accesibilidad WCAG:** Cumplimiento de las pautas de accesibilidad web (WCAG 2.1 nivel AA) en términos de contraste de color y legibilidad.
*   **RNF-USA-03: Feedback Visual:** El sistema debe informar siempre del estado de la interacción mediante notificaciones sutiles (*Toasts*) y estados de carga (*Skeletons*).

---

[Volver al Índice de Documentación](/README.md)

