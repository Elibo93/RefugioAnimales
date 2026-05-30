### Estrategia de Experiencia de Usuario (UX) - Refugio
---

El diseño del sistema se fundamenta en el **Diseño Centrado en el Usuario (UCD)**, priorizando la reducción de la carga cognitiva para el personal del refugio y la conexión emocional para los adoptantes. La UX no solo busca que la herramienta sea fácil de usar, sino que contribuya activamente al bienestar animal mediante procesos eficientes.

---

#### 1. Arquetipos de Usuario (User Personas)

Hemos identificado tres perfiles clave con necesidades diferenciadas:
*   **Marta (Administradora):** Necesita una visión global del refugio, informes rápidos y control legal de las adopciones. Valora la **trazabilidad y la seguridad**.
*   **Carlos (Voluntario):** Utiliza la app desde el patio con guantes o en movimiento. Necesita registrar tareas en segundos. Valora la **rapidez y el uso en dispositivos móviles**.
*   **Elena (Adoptante):** Busca un compañero de vida. Se siente abrumada por los procesos burocráticos. Valora la **transparencia, la cercanía y la facilidad de contacto**.

---

#### 2. Principios de Usabilidad y Heurísticas
Aplicamos las reglas de oro de la usabilidad para garantizar una interfaz robusta:
*   **Prevención de Errores:** Los formularios utilizan validaciones en tiempo real (ej. formato de DNI o microchip) para evitar que el usuario guarde datos incorrectos.
*   **Reconocimiento antes que Recuerdo:** Uso de iconos estandarizados (perro para animales, usuario para voluntarios) y menús persistentes que evitan que el usuario tenga que memorizar rutas.
*   **Flexibilidad y Eficiencia de Uso:** Los usuarios expertos pueden usar atajos y acciones rápidas en las tablas, mientras que los novatos son guiados por flujos paso a paso. Adicionalmente, se reduce la fricción y la carga cognitiva del usuario al integrar **Google OAuth2** (Single Sign-On), permitiendo un acceso rápido y seguro que elimina la fatiga de recordar y gestionar nuevas contraseñas.

---

#### 3. Arquitectura de Información
La información se organiza de forma jerárquica para evitar el desorden visual:
1.  **Nivel 1 (Dashboard):** Alertas críticas y estado actual (ej: "3 animales requieren medicación hoy").
2.  **Nivel 2 (Módulos):** Acceso directo a Animales, Voluntarios o Adopciones.
3.  **Nivel 3 (Fichas):** Información detallada con pestañas para separar datos biológicos, médicos y legales.

---

#### 4. Diseño Emocional y Conexión
Dada la naturaleza del refugio, la UX debe ser empática:
*   **Narrativa Visual:** Las fichas de animales no solo muestran datos técnicos, sino que priorizan la "historia" y la fotografía del animal para fomentar el vínculo.
*   **Mensajes Positivos:** Las confirmaciones de adopción utilizan un tono celebratorio, reforzando la acción positiva del adoptante.
*   **Micro-interacciones:** Animaciones suaves al marcar una tarea como completada, proporcionando una sensación de progreso y satisfacción al voluntario.

---

#### 5. Accesibilidad e Inclusión
*   **Contraste y Legibilidad:** Relación de contraste 4.5:1 mín. para textos, asegurando que sea legible bajo luz solar directa (patio).
*   **Navegación por Teclado:** Toda la interfaz es accesible mediante tabulación, facilitando el uso para personas con movilidad reducida.
*   **Lenguaje Claro:** Evitamos tecnicismos informáticos en favor de términos del sector del bienestar animal.

---

[Volver al Índice de Documentación](/README.md)

