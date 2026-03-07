package es.refugio.animales.refugio.domain.error;

import es.refugio.animales.common.domain.error.EntityNotFoundException;

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

















