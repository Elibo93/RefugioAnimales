### Estrategia de Implantación - Proyecto Refugio
---

Este apartado define la metodología para garantizar que el sistema del refugio se despliegue de forma profesional, reproducible y segura.

#### Contenedorización (Docker)
Todo el sistema (API + DB) está empaquetado en contenedores Docker.
* **Consistencia:** El software funciona igual en el portátil de desarrollo que en el servidor final del refugio.
* **Aislamiento:** Evita conflictos con otros programas del servidor.

#### Gestión del Ciclo de Vida (Maven)
Usamos **Apache Maven** para compilar el código y gestionar las librerías. El artefacto final es un "Fat JAR" que el refugio puede ejecutar con un simple comando.

#### Estrategia de Versionado (GitFlow)
* **`main`**: Código estable y verificado. Es lo que el refugio usa a diario.
* **`develop`**: Integración de nuevas mejoras (ej. módulo de vacunas).
* **`feature/*`**: Ramas para desarrollar funcionalidades concretas.

Esta estructura protege el funcionamiento diario del refugio mientras se desarrollan nuevas capacidades para el sistema.

---

[Volver](/README.md)
