package es.refugio.animales.vista.infraestructure.web.enums;

/**
 * Representa los posibles atributos que podemos usar
 * en los modelos de la vista
 */
public enum ModelAttribute {
    Persona_LIST("personas"),
    Voluntario_LIST("voluntarios"),
    Animal_LIST("animales"),
    Adopcion_LIST("adopciones"),

    REFUGIO_ANIMALES("Refugio de Animales"),

    SINGLE_Persona("Persona"),
    SINGLE_Voluntario("Voluntario"),
    SINGLE_Animal("Animal"),
    SINGLE_Adopcion("Adopcion"),

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
















