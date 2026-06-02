package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class HistorialMedicoNotFoundException extends EntityNotFoundException {
    
    public static final String ENTIDAD = "Historial Médico";

    public HistorialMedicoNotFoundException() {
        super(ENTIDAD);
    }

    public HistorialMedicoNotFoundException(int id) {
        super(ENTIDAD, id);
    }
}
