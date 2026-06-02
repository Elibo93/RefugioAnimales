package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class SolicitudAdopcionNotFoundException extends EntityNotFoundException {
    
    public static final String ENTIDAD = "Solicitud de Adopción";

    public SolicitudAdopcionNotFoundException() {
        super(ENTIDAD);
    }

    public SolicitudAdopcionNotFoundException(int id) {
        super(ENTIDAD, id);
    }
}
