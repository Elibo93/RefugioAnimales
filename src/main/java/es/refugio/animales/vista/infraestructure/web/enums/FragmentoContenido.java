package es.refugio.animales.vista.infraestructure.web.enums;

public enum FragmentoContenido {

    Persona_LIST("fragments/content/personas-lista"),
    Persona_FORM("fragments/content/Persona-formulario"),
    Persona_CREATED("fragments/content/Persona-creado"),

    Voluntario_LIST("fragments/content/voluntarios-lista"),
    Voluntario_FORM("fragments/content/Voluntario-formulario"),

    Animal_LIST("fragments/content/animales-lista"),
    Animal_FORM("fragments/content/Animal-formulario"),

    Adopcion_LIST("fragments/content/adopciones-lista"),
    Adopcion_FORM("fragments/content/Adopcion-formulario"),

    HOME_VIEW("fragments/content/index");

    private final String path;

    FragmentoContenido(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
















