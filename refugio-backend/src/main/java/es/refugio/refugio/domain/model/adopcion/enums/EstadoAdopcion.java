package es.refugio.refugio.domain.model.adopcion.enums;

public enum EstadoAdopcion {
    PENDIENTE_FIRMA("Pendiente de firma"),
    EN_PERIODO_ADAPTACION("En periodo de adaptación"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String nombre;

    EstadoAdopcion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
