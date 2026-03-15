package es.refugio.animales.vista.infraestructure.web.enums;

public enum FragmentoContenido {

    Persona_LIST("fragments/content/personas-lista"),
    Persona_FORM("fragments/content/persona-formulario"),
    Persona_CREATED("fragments/content/persona-creada"),

    Voluntario_LIST("fragments/content/voluntarios-lista"),
    Voluntario_FORM("fragments/content/voluntario-formulario"),

    Animal_LIST("fragments/content/animales-lista"),
    Animal_FORM("fragments/content/animal-formulario"),

    Adopcion_LIST("fragments/content/adopciones-lista"),
    Adopcion_FORM("fragments/content/adopcion-formulario"),

    HOME_VIEW("fragments/content/index");

    private final String path;

    FragmentoContenido(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
















