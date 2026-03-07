package es.refugio.animales.vista.infraestructure.web.enums;

/**
 * Contiene el listado de plantillas html
 */
public enum ThymTemplates {

    Persona_LIST("personas-lista"),
    Persona_LIST_PDF("pdf/personas-lista"),
    Persona_FORM("Persona-formulario"),
    Persona_DETAIL("personas-detalle"),
    Persona_CREATED("Persona-creado"),

    Voluntario_LIST("voluntarios-lista"),
    Voluntario_LIST_PDF("pdf/voluntarios-lista"),
    Voluntario_FORM("Voluntario-formulario"),

    Animal_LIST("animales-lista"),
    Animal_LIST_PDF("pdf/animales-lista"),
    Animal_FORM("Animal-formulario"),

    Adopcion_LIST("adopciones-lista"),
    Adopcion_LIST_PDF("pdf/adopciones-lista"),
    Adopcion_FORM("Adopcion-formulario"),

    HOME_VIEW("index"),
    MAIN_LAYOUT("main-layout"),
    ERROR_GENERIC("error/error-general");

    private final String path;

    ThymTemplates(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
















