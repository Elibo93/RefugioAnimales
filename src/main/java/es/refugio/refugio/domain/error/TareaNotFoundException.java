package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class TareaNotFoundException extends EntityNotFoundException {
    
    public static final String ENTIDAD = "Tarea";

    public TareaNotFoundException() {
        super(ENTIDAD);
    }

    public TareaNotFoundException(int id) {
        super(ENTIDAD, id);
    }
}
