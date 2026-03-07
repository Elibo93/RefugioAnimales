### Diseño de Interacción (IxD) - Refugio de Animales
---

El diseño de la interacción se ha definido para garantizar una comunicación fluida y segura entre el personal del refugio y la aplicación, minimizando errores en la gestión de seres vivos y procesos legales de adopción.

---

#### 1. Estrategia de Interacción
La interfaz prioriza la facilidad de uso para voluntarios que pueden no estar familiarizados con sistemas complejos.

* **Jerarquía Visual y Affordance:** Los botones de "Registrar Entrada" o "Solicitar Adopción" destacan visualmente. Las acciones críticas como "Eliminar Animal" o "Cancelar Adopción" utilizan colores de advertencia (rojo) para prevenir clics accidentales.
* **Diseño de Formularios:** La carga de datos de animales (chip, especie, salud) se guía mediante etiquetas claras y ejemplos en los placeholders, asegurando que la información vital se registre correctamente.

---

#### 2. Seguridad y Prevención de Errores
Dada la sensibilidad de los datos manejados, se implementan salvaguardas heurísticas:

* **Confirmación de Acciones Críticas:** Operaciones como dar de baja a un voluntario o eliminar la ficha de un animal requieren una **confirmación mediante diálogo modal**. Esto evita la pérdida accidental de historial médico o de trazabilidad.

---

#### 3. Retroalimentación del Sistema (System Feedback)
El sistema mantiene al usuario informado sobre el estado de las gestiones en tiempo real:

* **Respuesta Inmediata:** 
    * *Éxito:* Notificaciones visuales al guardar una nueva ficha de animal o completar un seguimiento.
    * *Error:* Mensajes de validación específicos si falta el número de chip o si el formato de la fecha de ingreso es incorrecto.
* **Trazabilidad:** El usuario siempre recibe confirmación de que sus cambios han sido persistidos en el servidor, generando confianza en la herramienta.

---

#### 4. Objetivo: Seguridad y Eficiencia
El diseño busca reducir la carga cognitiva del voluntariado, permitiendo que la gestión administrativa sea un apoyo y no una barrera en su labor diaria de cuidado animal.

---

[Volver](/README.md)
