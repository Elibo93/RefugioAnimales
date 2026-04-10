package es.refugio.frontend.web.enums;

/**
 * Rutas de templates de Thymeleaf.
 * Solo se incluyen las entradas que realmente se usan en los controllers:
 *  - MAIN_LAYOUT: retorno principal de todos los controllers de vista.
 *  - X_LIST_PDF:  template para la generación de PDFs via templateEngine.process().
 */
public enum ThymTemplates {

    // ─── Layout principal ────────────────────────────────────────────────────
    MAIN_LAYOUT("main-layout"),

    // ─── PDFs ────────────────────────────────────────────────────────────────
    Persona_LIST_PDF("pdf/personas-lista"),
    Voluntario_LIST_PDF("pdf/voluntarios-lista"),
    Animal_LIST_PDF("pdf/animales-lista"),
    Adopcion_LIST_PDF("pdf/adopciones-lista"),
    Donacion_LIST_PDF("pdf/donaciones-lista"),
    Historial_LIST_PDF("pdf/historiales-medicos-lista"),
    Solicitud_LIST_PDF("pdf/solicitudes-adopcion-lista"),
    Tarea_LIST_PDF("pdf/tareas-lista");

    private final String path;

    ThymTemplates(String path) { this.path = path; }

    public String getPath() { return this.path; }
}
