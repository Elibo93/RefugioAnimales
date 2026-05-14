### Diseño de Interacción (IxD) - Refugio de Animales
---

El **Diseño de Interacción (IxD)** de esta aplicación se centra en crear una relación eficiente y emocionalmente positiva entre el personal del refugio, los voluntarios, los visitantes, los adoptantes y el sistema. El objetivo es que la tecnología actúe como un facilitador invisible en la misión crítica de salvar y cuidar animales.

---

#### 1. Metáfora del Sistema: "El Cuaderno Digital de Bitácora"
La interfaz se concibe como una evolución del cuaderno de campo tradicional del refugio. 
- **Simplicidad:** La información fluye de manera lineal y clara.
- **Trazabilidad:** Cada interacción deja una "huella" (historial), emulando la rigurosidad de los registros legales y médicos necesarios en un refugio.

---

#### 2. Patrones de Interacción Principales

Para garantizar la consistencia y reducir la curva de aprendizaje, se han implementado los siguientes patrones:

*   **Modales de Confirmación (Safe-Guard):** 
    *   *Uso:* Eliminaciones, cambios de estado críticos (ej. marcar un animal como fallecido o adoptado) y cierre de sesiones con cambios pendientes.
    *   *Comportamiento:* Bloqueo del scroll de fondo y enfoque automático en el botón de "Cancelar" para evitar pulsaciones accidentales.
*   **Notificaciones Emergentes (Toasts):** 
    *   *Uso:* Feedback no intrusivo tras acciones exitosas (ej. "Perfil actualizado correctamente").
    *   *Uso:* Alertas de error (ej. "No se ha podido conectar con el servidor de correos").
*   **Formularios con Validación Proactiva:**
    *   Los campos obligatorios muestran indicadores visuales inmediatos.
    *   Uso de *placeholders* descriptivos y *tooltips* de ayuda en campos complejos como el formato del microchip.
*   **Filtros en Tiempo Real (Exploración):**
    *   En los catálogos de animales, los filtros actúan de forma inmediata o mediante un botón de "Aplicar" claramente destacado, permitiendo al usuario encontrar rápidamente compatibilidades.

---

#### 3. Flujos de Interacción Críticos

##### 3.1. Proceso de Adopción (El "Embudo" de Decisión)
1.  **Descubrimiento (Visitante):** El usuario explora el catálogo público mediante *scroll* infinito o paginación clara.
2.  **Interés (Visitante/Adoptante):** Al pulsar en un animal, una transición suave (fade-in) presenta la ficha técnica detallada.
3.  **Acción (Adoptante):** El botón "Solicitar Adopción" (solo disponible tras el inicio de sesión) abre un formulario pre-cumplimentado con los datos del perfil.
4.  **Confirmación (Adoptante):** Tras el envío, el sistema redirige a una pantalla de "Éxito" con los siguientes pasos explicados.

##### 3.2. Gestión de Tareas (Voluntariado)
1.  **Vistazo rápido:** El Dashboard muestra las tareas del día con códigos de colores (Urgente = Rojo, Rutina = Azul).
2.  **Ejecución:** Al marcar una tarea como completada, se requiere una breve nota de observación si el animal presentó alguna anomalía.

---

#### 4. Estados de la Interfaz

El sistema comunica siempre en qué estado se encuentra para evitar la incertidumbre:
*   **Estado de Carga (Loading):** Uso de *skeletons* (esqueletos de carga) en lugar de *spinners* tradicionales para reducir la percepción del tiempo de espera.
*   **Estado Vacío (Empty State):** Cuando no hay animales en una búsqueda o tareas pendientes, se muestran ilustraciones amigables y mensajes motivadores ("¡Buen trabajo! No hay tareas pendientes").
*   **Estado de Error:** Páginas 404 y 500 personalizadas con lenguaje humano y un botón de "Volver al inicio".

---

#### 5. Accesibilidad y Ergonomía Visual

*   **Contraste y Legibilidad:** Uso de la tipografía *Plus Jakarta Sans* por su alta legibilidad en pantallas pequeñas. Cumplimiento de ratios de contraste WCAG 2.1 AA.
*   **Navegación por Teclado:** Orden de tabulación lógico en todos los formularios y estados de *focus* claramente visibles (contorno azul brillante).
*   **Diseño Responsive (Mobile First):** Los botones táctiles tienen un área mínima de 44x44px para facilitar el uso en tablets durante las rondas por las jaulas.

---

#### 6. Micro-interacciones y Feedback Sensorial

*   **Hover Effects:** Los botones y tarjetas de animales responden sutilmente al pasar el cursor, indicando su interactividad.
*   **Transiciones:** Cambios de vista fluidos para mantener el contexto espacial del usuario dentro de la aplicación.

---

[Volver al Índice de Documentación](/README.md)
