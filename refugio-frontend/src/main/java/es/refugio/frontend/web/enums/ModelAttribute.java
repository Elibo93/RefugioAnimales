package es.refugio.frontend.web.enums;

/**
 * Claves de atributos del modelo Thymeleaf usadas en los controllers de vista.
 * ERROR_MESSAGE y SUCCESS_MESSAGE se eliminaron: los controllers usan directamente
 * las cadenas "errorMessage" / "successMessage" en model.addAttribute().
 */
public enum ModelAttribute {

    // ─── Listas ──────────────────────────────────────────────────────────────
    Persona_LIST("personas"),
    Voluntario_LIST("voluntarios"),
    Animal_LIST("animales"),
    Adopcion_LIST("adopciones"),
    Donacion_LIST("donaciones"),
    Historial_LIST("historiales"),
    Solicitud_LIST("solicitudes"),
    Tarea_LIST("tareas"),
    Adoptante_LIST("adoptantes"),

    // ─── Objeto único ────────────────────────────────────────────────────────
    SINGLE_Persona("persona"),
    SINGLE_Voluntario("voluntario"),
    SINGLE_Animal("animal"),
    SINGLE_Adopcion("adopcion"),
    SINGLE_Donacion("donacion"),
    SINGLE_Historial("historial"),
    SINGLE_Solicitud("solicitud"),
    SINGLE_Tarea("tarea"),
    SINGLE_Adoptante("adoptante"),

    // ─── Layout ──────────────────────────────────────────────────────────────
    FRAGMENTO_CONTENIDO("content");

    private final String name;

    ModelAttribute(String name) { this.name = name; }

    public String getName() { return this.name; }
}
