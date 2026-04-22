package es.refugio.frontend.web.constants;

/**
 * Rutas de acceso desde el navegador — idéntico al original del monolito.
 */
public interface WebRoutes {

    // PRINCIPAL
    String HOME = "/web/home";

    // PERSONAS
    String PERSONAS_BASE     = "/web/personas";
    String PERSONAS_NUEVO    = "/web/personas/nuevo";
    String PERSONAS_EDITAR   = "/web/personas/{id}/editar";
    String PERSONAS_PDF      = "/web/personas/pdf";
    String PERSONAS_ELIMINAR = "/web/personas/{id}/borrar";

    // ADOPTANTES
    String ADOPTANTES_BASE                 = "/web/adoptantes";
    String ADOPTANTES_MODAL_CONVERTIR      = "/web/adoptantes/modal-convertir";
    String ADOPTANTES_CONVERTIR_Y_SOLICITAR = "/web/adoptantes/convertir-y-solicitar";
    String ADOPTANTES_NUEVO                = "/web/adoptantes/nuevo";
    String ADOPTANTES_MODAL_EDITAR  = "/web/adoptantes/modal-editar/{id}";
    String ADOPTANTES_EDITAR               = "/web/adoptantes/{id}/editar";
    String ADOPTANTES_ELIMINAR             = "/web/adoptantes/{id}/borrar";
    String ADOPTANTES_PDF                  = "/web/adoptantes/pdf";
    String ADOPTANTES_APROBAR              = "/web/adoptantes/{id}/aprobar";
    String ADOPTANTES_RECHAZAR             = "/web/adoptantes/{id}/rechazar";

    // VOLUNTARIOS
    String VOLUNTARIOS_BASE     = "/web/voluntarios";
    String VOLUNTARIOS_MODAL_EDITAR = "/web/voluntarios/modal-editar/{id}";
    String VOLUNTARIOS_NUEVO    = "/web/voluntarios/nuevo";
    String VOLUNTARIOS_EDITAR   = "/web/voluntarios/{id}/editar";
    String VOLUNTARIOS_PDF      = "/web/voluntarios/pdf";
    String VOLUNTARIOS_ELIMINAR = "/web/voluntarios/{id}/borrar";

    // ANIMALES
    String ANIMALES_BASE     = "/web/animales";
    String ANIMALES_NUEVO    = "/web/animales/nuevo";
    String ANIMALES_EDITAR   = "/web/animales/{id}/editar";
    String ANIMALES_PDF      = "/web/animales/pdf";
    String ANIMALES_ELIMINAR = "/web/animales/{id}/borrar";

    // ADOPCIONES
    String ADOPCIONES_BASE     = "/web/adopciones";
    String ADOPCIONES_NUEVA    = "/web/adopciones/nueva";
    String ADOPCIONES_EDITAR   = "/web/adopciones/{id}/editar";
    String ADOPCIONES_ELIMINAR = "/web/adopciones/{id}/borrar";
    String ADOPCIONES_PDF      = "/web/adopciones/pdf";

    // DONACIONES
    String DONACIONES_BASE     = "/web/donaciones";
    String DONACIONES_NUEVA    = "/web/donaciones/nueva";
    String DONACIONES_EDITAR   = "/web/donaciones/{id}/editar";
    String DONACIONES_ELIMINAR = "/web/donaciones/{id}/borrar";
    String DONACIONES_PDF      = "/web/donaciones/pdf";

    // HISTORIAL MÉDICO
    String HISTORIALES_BASE     = "/web/historiales";
    String HISTORIALES_NUEVO    = "/web/historiales/nuevo";
    String HISTORIALES_EDITAR   = "/web/historiales/{id}/editar";
    String HISTORIALES_ELIMINAR = "/web/historiales/{id}/borrar";
    String HISTORIALES_PDF      = "/web/historiales/pdf";

    // SOLICITUDES ADOPCIÓN
    String SOLICITUDES_BASE         = "/web/solicitudes";
    String SOLICITUDES_NUEVA        = "/web/solicitudes/nueva";
    String SOLICITUDES_MODAL_NUEVA  = "/web/solicitudes/modal-nueva";
    String SOLICITUDES_MODAL_EDITAR = "/web/solicitudes/modal-editar/{id}";
    String SOLICITUDES_EDITAR       = "/web/solicitudes/{id}/editar";
    String SOLICITUDES_ELIMINAR     = "/web/solicitudes/{id}/borrar";
    String SOLICITUDES_PDF          = "/web/solicitudes/pdf";
    String SOLICITUDES_PUBLICO_REGISTRO = "/web/solicitudes/publico/registro-y-adopcion";
    String SOLICITUDES_APROBAR      = "/web/solicitudes/{id}/aprobar";
    String SOLICITUDES_RECHAZAR     = "/web/solicitudes/{id}/rechazar";
    String SOLICITUDES_OPCIONES     = "/web/solicitudes/publico/opciones";
    String SOLICITUDES_CONVERTIR    = "/web/solicitudes/publico/convertir";
    String SOLICITUDES_DIRECTA_FORM = "/web/solicitudes/publico/directa/formulario";
    String SOLICITUDES_DIRECTA      = "/web/solicitudes/publico/directa";

    // TAREAS
    String TAREAS_BASE     = "/web/tareas";
    String TAREAS_NUEVA    = "/web/tareas/nueva";
    String TAREAS_EDITAR   = "/web/tareas/{id}/editar";
    String TAREAS_ELIMINAR = "/web/tareas/{id}/borrar";
    String TAREAS_PDF      = "/web/tareas/pdf";
}
