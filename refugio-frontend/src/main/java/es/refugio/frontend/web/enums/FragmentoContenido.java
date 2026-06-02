package es.refugio.frontend.web.enums;

public enum FragmentoContenido {

    Animal_LIST("fragments/content/animales/animales-lista"),
    Animal_FORM("fragments/content/animales/animal-formulario"),
    Animal_DETALLE("fragments/content/animales/animal-detalle"),

    Persona_LIST("fragments/content/personas/personas-lista"),
    Persona_FORM("fragments/content/personas/persona-formulario"),
    Persona_DETALLE("fragments/content/personas/persona-detalle"),
    Persona_CREATED("fragments/content/personas/persona-creada"),
    USUARIO_SUGERENCIAS("fragments/content/personas/usuarios-sugerencias"),

    Voluntario_LIST("fragments/content/voluntarios/voluntarios-lista"),
    Voluntario_FORM("fragments/content/voluntarios/voluntario-formulario"),
    VOLUNTARIO_SUGERENCIAS("fragments/content/voluntarios/voluntarios-sugerencias"),

    Adoptante_LIST("fragments/content/adoptantes/adoptantes-lista"),
    Adoptante_FORM("fragments/content/adoptantes/adoptante-formulario"),

    Adopcion_LIST("fragments/content/adopciones/adopciones-lista"),
    Adopcion_FORM("fragments/content/adopciones/adopcion-formulario"),

    Donacion_LIST("fragments/content/donaciones/donaciones-lista"),
    Donacion_FORM("fragments/content/donaciones/donacion-formulario"),
    Donacion_GRACIAS("fragments/content/donaciones/donacion-gracias"),
    Donacion_PASARELA("fragments/content/donaciones/donacion-pasarela"),

    Historial_LIST("fragments/content/historial_medico/historiales-medicos-lista"),
    Historial_FORM("fragments/content/historial_medico/historial-medico-formulario"),

    Solicitud_LIST("fragments/content/solicitudes_adopcion/solicitudes-adopcion-lista"),
    Solicitud_FORM("fragments/content/solicitudes_adopcion/solicitud-adopcion-formulario"),
    Solicitud_DETALLE("fragments/content/solicitudes_adopcion/solicitud-adopcion-detalle"),

    Tarea_LIST("fragments/content/tareas/tareas-lista"),
    Tarea_FORM("fragments/content/tareas/tarea-formulario"),
    Tarea_HISTORIAL("fragments/content/tareas/tarea-historial"),
    Solicitud_REGISTRO("fragments/content/solicitudes_adopcion/solicitud-registro-form"),
    Solicitud_OPCIONES("fragments/content/solicitudes_adopcion/solicitud-opciones"),
    Solicitud_CONVERSION("fragments/content/solicitudes_adopcion/solicitud-conversion-form"),
    Solicitud_DIRECTA_FORM("fragments/content/solicitudes_adopcion/solicitud-directa-form"),

    HOME_VIEW("fragments/content/core/index"),
    MIS_ADOPTADOS_VACIO("fragments/content/adoptantes/mis-adoptados-vacio"),
    MIS_ADOPTADOS_LISTA("fragments/content/adoptantes/mis-adoptados-lista"),
    Notificacion_LIST("fragments/content/notificaciones/notificaciones-lista");

    private final String path;

    FragmentoContenido(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
