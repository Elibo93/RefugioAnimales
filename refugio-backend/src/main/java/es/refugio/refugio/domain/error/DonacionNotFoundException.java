package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class DonacionNotFoundException extends EntityNotFoundException {
    
    public static final String ENTIDAD = "Donación";

    public DonacionNotFoundException() {
        super(ENTIDAD);
    }

    public DonacionNotFoundException(int id) {
        super(ENTIDAD, id);
    }
}
