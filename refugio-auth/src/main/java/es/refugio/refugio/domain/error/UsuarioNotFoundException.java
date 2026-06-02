package es.refugio.refugio.domain.error;

import es.refugio.common.domain.error.EntityNotFoundException;

public class UsuarioNotFoundException extends EntityNotFoundException {

    public static final String ENTIDAD = "Usuario";

    public UsuarioNotFoundException() {
        super(ENTIDAD);
    }

    public UsuarioNotFoundException(int id) {
        super(ENTIDAD, id);
    }

}