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
}
