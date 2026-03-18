package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class AnimalNotFoundException extends EntityNotFoundException{

    //Atributos
    public static final String ENTIDAD = "Animal";

    public AnimalNotFoundException() {
        super(ENTIDAD);
    }

    public AnimalNotFoundException(int id) {
        super(ENTIDAD, id);
    }
}

















