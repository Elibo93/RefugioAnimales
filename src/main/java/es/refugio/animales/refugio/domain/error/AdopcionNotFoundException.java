package es.refugio.animales.refugio.domain.error;

import es.refugio.animales.common.domain.error.EntityNotFoundException;

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

















