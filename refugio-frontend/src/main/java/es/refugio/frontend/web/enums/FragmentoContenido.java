package es.refugio.frontend.web.enums;

public enum FragmentoContenido {

    Animal_LIST("fragments/content/animales-lista"),
    Animal_FORM("fragments/content/animal-formulario"),
    Animal_DETALLE("fragments/content/animal-detalle"),

    Persona_LIST("fragments/content/personas-lista"),
    Persona_FORM("fragments/content/persona-formulario"),
    Persona_DETALLE("fragments/content/persona-detalle"),
    Persona_CREATED("fragments/content/persona-creada"),
    USUARIO_SUGERENCIAS("fragments/content/usuarios-sugerencias"),

    Voluntario_LIST("fragments/content/voluntarios-lista"),
    Voluntario_FORM("fragments/content/voluntario-formulario"),
    VOLUNTARIO_SUGERENCIAS("fragments/content/voluntarios-sugerencias"),

    Adoptante_LIST("fragments/content/adoptantes-lista"),
    Adoptante_FORM("fragments/content/adoptante-formulario"),

    Adopcion_LIST("fragments/content/adopciones-lista"),
    Adopcion_FORM("fragments/content/adopcion-formulario"),

    Donacion_LIST("fragments/content/donaciones-lista"),
    Donacion_FORM("fragments/content/donacion-formulario"),
    Donacion_GRACIAS("fragments/content/donacion-gracias"),
    Donacion_PASARELA("fragments/content/donacion-pasarela"),

    Historial_LIST("fragments/content/historiales-medicos-lista"),
    Historial_FORM("fragments/content/historial-medico-formulario"),

    Solicitud_LIST("fragments/content/solicitudes-adopcion-lista"),
    Solicitud_FORM("fragments/content/solicitud-adopcion-formulario"),

    Tarea_LIST("fragments/content/tareas-lista"),
    Tarea_FORM("fragments/content/tarea-formulario"),
    Solicitud_REGISTRO("fragments/content/solicitud-registro-form"),
    Solicitud_OPCIONES("fragments/content/solicitud-opciones"),
    Solicitud_CONVERSION("fragments/content/solicitud-conversion-form"),
    Solicitud_DIRECTA_FORM("fragments/content/solicitud-directa-form"),

    HOME_VIEW("fragments/content/index"),
    MIS_ADOPTADOS_VACIO("fragments/content/mis-adoptados-vacio"),
    MIS_ADOPTADOS_LISTA("fragments/content/mis-adoptados-lista");

    private final String path;

    FragmentoContenido(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
