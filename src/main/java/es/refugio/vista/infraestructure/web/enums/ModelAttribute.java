package es.refugio.vista.infraestructure.web.enums;

/**
 * Representa los posibles atributos que podemos usar
 * en los modelos de la vista
 */
public enum ModelAttribute {
    Persona_LIST("personas"),
    Voluntario_LIST("voluntarios"),
    Animal_LIST("animales"),
    Adopcion_LIST("adopciones"),
    Donacion_LIST("donaciones"),
    Historial_LIST("historiales"),
    Solicitud_LIST("solicitudes"),
    Tarea_LIST("tareas"),

    REFUGIO_ANIMALES("refugio de animales"),

    SINGLE_Persona("persona"),
    SINGLE_Voluntario("voluntario"),
    SINGLE_Animal("animal"),
    SINGLE_Adopcion("adopcion"),
    SINGLE_Donacion("donacion"),
    SINGLE_Historial("historial"),
    SINGLE_Solicitud("solicitud"),
    SINGLE_Tarea("tarea"),

    FRAGMENTO_CONTENIDO("content"),

    ERROR_MESSAGE("errorMsg"),
    SUCCESS_MESSAGE("successMsg");

    private final String name;

    ModelAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
