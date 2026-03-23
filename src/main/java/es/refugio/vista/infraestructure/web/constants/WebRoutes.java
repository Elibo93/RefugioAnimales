package es.refugio.vista.infraestructure.web.constants;

/**
 * Contiene las rutas de acceso desde el navegador
 */
public interface WebRoutes {

    // PRINCIPAL
    public static final String HOME = "/web/home";

    // PERSONAS
    public static final String personas_BASE = "/web/personas";
    public static final String personas_NUEVO = "/web/personas/nuevo";
    public static final String personas_EDITAR = "/web/personas/{id}/editar";
    public static final String personas_PDF = "/web/personas/pdf";
    public static final String personas_ELIMINAR = "/web/personas/{id}/borrar";

    // ADOPTANTES
    public static final String adoptantes_MODAL_CONVERTIR = "/web/adoptantes/modal-convertir";
    public static final String adoptantes_CONVERTIR_Y_SOLICITAR = "/web/adoptantes/convertir-y-solicitar";

    // VOLUNTARIOS
    public static final String voluntarios_BASE = "/web/voluntarios";
    public static final String voluntarios_NUEVO = "/web/voluntarios/nuevo";
    public static final String voluntarios_EDITAR = "/web/voluntarios/{id}/editar";
    public static final String voluntarios_PDF = "/web/voluntarios/pdf";
    public static final String voluntarios_ELIMINAR = "/web/voluntarios/{id}/borrar";

    // ANIMALES
    public static final String animales_BASE = "/web/animales";
    public static final String animales_NUEVO = "/web/animales/nuevo";
    public static final String animales_EDITAR = "/web/animales/{id}/editar";
    public static final String animales_PDF = "/web/animales/pdf";
    public static final String animales_ELIMINAR = "/web/animales/{id}/borrar";

    // ADOPCIONES
    public static final String adopciones_BASE = "/web/adopciones";
    public static final String adopciones_NUEVA = "/web/adopciones/nueva";
    public static final String adopciones_EDITAR = "/web/adopciones/{id}/editar";
    public static final String adopciones_ELIMINAR = "/web/adopciones/{id}/borrar";
    public static final String adopciones_PDF = "/web/adopciones/pdf";

    // DONACIONES
    public static final String donaciones_BASE = "/web/donaciones";
    public static final String donaciones_NUEVA = "/web/donaciones/nueva";
    public static final String donaciones_EDITAR = "/web/donaciones/{id}/editar";
    public static final String donaciones_ELIMINAR = "/web/donaciones/{id}/borrar";
    public static final String donaciones_PDF = "/web/donaciones/pdf";

    // HISTORIAL MÉDICO
    public static final String historiales_BASE = "/web/historiales";
    public static final String historiales_NUEVO = "/web/historiales/nuevo";
    public static final String historiales_EDITAR = "/web/historiales/{id}/editar";
    public static final String historiales_ELIMINAR = "/web/historiales/{id}/borrar";
    public static final String historiales_PDF = "/web/historiales/pdf";

    // SOLICITUDES ADOPCIÓN
    public static final String solicitudes_BASE = "/web/solicitudes";
    public static final String solicitudes_NUEVA = "/web/solicitudes/nueva";
    public static final String solicitudes_EDITAR = "/web/solicitudes/{id}/editar";
    public static final String solicitudes_ELIMINAR = "/web/solicitudes/{id}/borrar";
    public static final String solicitudes_PDF = "/web/solicitudes/pdf";

    // TAREAS
    public static final String tareas_BASE = "/web/tareas";
    public static final String tareas_NUEVA = "/web/tareas/nueva";
    public static final String tareas_EDITAR = "/web/tareas/{id}/editar";
    public static final String tareas_ELIMINAR = "/web/tareas/{id}/borrar";
    public static final String tareas_PDF = "/web/tareas/pdf";
}
