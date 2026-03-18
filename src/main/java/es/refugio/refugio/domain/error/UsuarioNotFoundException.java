package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class UsuarioNotFoundException extends EntityNotFoundException {

    // Atributos
    public static final String ENTIDAD = "Persona";

    public UsuarioNotFoundException() {
        super(ENTIDAD);
    }

    public UsuarioNotFoundException(int id) {
        super(ENTIDAD, id);
    }

}

















