package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class AdopcionNotFoundException extends EntityNotFoundException{

    //Atributos
    public static final String ENTIDAD = "Adopcion";

    public AdopcionNotFoundException() {
        super(ENTIDAD);
    }

    public AdopcionNotFoundException(int id) {
        super(ENTIDAD, id);
    }


}

















