package es.refugio.animales.refugio.domain.error;

import es.refugio.animales.common.domain.error.EntityNotFoundException;

public class PersonaNotFoundException extends EntityNotFoundException {

    // Atributos
    public static final String ENTIDAD = "Persona";

    public PersonaNotFoundException() {
        super(ENTIDAD);
    }

    public PersonaNotFoundException(int id) {
        super(ENTIDAD, id);
    }

}

















