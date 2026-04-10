package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class AdoptanteNotFoundException extends EntityNotFoundException {

    // Atributos
    public static final String ENTIDAD = "Adoptante";

    public AdoptanteNotFoundException() {
        super(ENTIDAD);
    }

    public AdoptanteNotFoundException(int id) {
        super(ENTIDAD, id);
    }
}