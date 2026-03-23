package es.refugio.refugio.domain.error;

public class SolicitudAdopcionEstadoInvalidoException extends RuntimeException {

    public SolicitudAdopcionEstadoInvalidoException(String estadoActual) {
        super("La solicitud no se puede procesar porque está en estado: " + estadoActual + ". Solo se pueden aprobar o rechazar solicitudes en estado PENDIENTE.");
    }
}
