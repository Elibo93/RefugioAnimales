package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class VoluntarioNotFoundException extends EntityNotFoundException {

    // Atributos
    public static final String ENTIDAD = "Voluntario";

    public VoluntarioNotFoundException() {
        super(ENTIDAD);
    }

    public VoluntarioNotFoundException(int id) {
        super(ENTIDAD, id);
    }

}

















