### Reutilización y Eficiencia Frontend - Refugio
---

La interfaz del refugio se ha diseñado para ser consistente, reactiva y fácil de mantener mediante la reutilización de componentes web.

#### 1. Sistema de Fragmentos (Thymeleaf)
Utilizamos un **Layout Principal** que define la cabecera y el pie de página de toda la aplicación.
* **Fragmentos:** Elementos como el "Listado de Jaulas" o la "Ficha del Adoptante" se definen una sola vez y se insertan en diferentes pantallas, facilitando cambios globales instantáneos.

#### 2. Interactividad con HTMX
Para evitar recargas pesadas de página, usamos **HTMX**:
* **Actualización Parcial:** Cambiar el estado de un animal (de "En Tratamiento" a "Sano") actualiza solo esa fila de la tabla sin recargar toda la interfaz.
* **Reutilización de Lógica:** Las mismas respuestas del servidor sirven tanto para la carga inicial como para las actualizaciones dinámicas.

#### Conclusión
Este enfoque reduce drásticamente el código duplicado y permite que la aplicación se sienta como una herramienta moderna y fluida, optimizando el tiempo de respuesta para los voluntarios.

---

[Volver](/README.md)
