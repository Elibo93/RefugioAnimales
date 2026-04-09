package es.refugio.vista.infraestructure.web.constants;

/**
 * Contiene las rutas de acceso desde el navegador
 */
public interface WebRoutes {

    // PRINCIPAL
    public static final String HOME = "/web/home";

    // PERSONAS
    public static final String PERSONAS_BASE = "/web/personas";
    public static final String PERSONAS_NUEVO = "/web/personas/nuevo";
    public static final String PERSONAS_EDITAR = "/web/personas/{id}/editar";
    public static final String PERSONAS_PDF = "/web/personas/pdf";
    public static final String PERSONAS_ELIMINAR = "/web/personas/{id}/borrar";

    // ADOPTANTES
    public static final String ADOPTANTES_MODAL_CONVERTIR = "/web/adoptantes/modal-convertir";
    public static final String ADOPTANTES_CONVERTIR_Y_SOLICITAR = "/web/adoptantes/convertir-y-solicitar";

    // VOLUNTARIOS
    public static final String VOLUNTARIOS_BASE = "/web/voluntarios";
    public static final String VOLUNTARIOS_NUEVO = "/web/voluntarios/nuevo";
    public static final String VOLUNTARIOS_EDITAR = "/web/voluntarios/{id}/editar";
    public static final String VOLUNTARIOS_PDF = "/web/voluntarios/pdf";
    public static final String VOLUNTARIOS_ELIMINAR = "/web/voluntarios/{id}/borrar";

    // ANIMALES
    public static final String ANIMALES_BASE = "/web/animales";
    public static final String ANIMALES_NUEVO = "/web/animales/nuevo";
    public static final String ANIMALES_EDITAR = "/web/animales/{id}/editar";
    public static final String ANIMALES_PDF = "/web/animales/pdf";
    public static final String ANIMALES_ELIMINAR = "/web/animales/{id}/borrar";

    // ADOPCIONES
    public static final String ADOPCIONES_BASE = "/web/adopciones";
    public static final String ADOPCIONES_NUEVA = "/web/adopciones/nueva";
    public static final String ADOPCIONES_EDITAR = "/web/adopciones/{id}/editar";
    public static final String ADOPCIONES_ELIMINAR = "/web/adopciones/{id}/borrar";
    public static final String ADOPCIONES_PDF = "/web/adopciones/pdf";

    // DONACIONES
    public static final String DONACIONES_BASE = "/web/donaciones";
    public static final String DONACIONES_NUEVA = "/web/donaciones/nueva";
    public static final String DONACIONES_EDITAR = "/web/donaciones/{id}/editar";
    public static final String DONACIONES_ELIMINAR = "/web/donaciones/{id}/borrar";
    public static final String DONACIONES_PDF = "/web/donaciones/pdf";

    // HISTORIAL MÉDICO
    public static final String HISTORIALES_BASE = "/web/historiales";
    public static final String HISTORIALES_NUEVO = "/web/historiales/nuevo";
    public static final String HISTORIALES_EDITAR = "/web/historiales/{id}/editar";
    public static final String HISTORIALES_ELIMINAR = "/web/historiales/{id}/borrar";
    public static final String HISTORIALES_PDF = "/web/historiales/pdf";

    // SOLICITUDES ADOPCIÓN
    public static final String SOLICITUDES_BASE = "/web/solicitudes";
    public static final String SOLICITUDES_NUEVA = "/web/solicitudes/nueva";
    public static final String SOLICITUDES_MODAL_NUEVA = "/web/solicitudes/modal-nueva";
    public static final String SOLICITUDES_EDITAR = "/web/solicitudes/{id}/editar";
    public static final String SOLICITUDES_ELIMINAR = "/web/solicitudes/{id}/borrar";
    public static final String SOLICITUDES_PDF = "/web/solicitudes/pdf";

    // TAREAS
    public static final String TAREAS_BASE = "/web/tareas";
    public static final String TAREAS_NUEVA = "/web/tareas/nueva";
    public static final String TAREAS_EDITAR = "/web/tareas/{id}/editar";
    public static final String TAREAS_ELIMINAR = "/web/tareas/{id}/borrar";
    public static final String TAREAS_PDF = "/web/tareas/pdf";
}
