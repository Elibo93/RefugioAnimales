package es.refugio.refugio.infraestructure.web.dto.adoptante;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;
import es.refugio.refugio.infraestructure.web.validation.MinAge;

public record AdoptanteRequest(
        Integer usuarioId,
        String nombre,
        String apellido,
        // @ValidDni String dni,
        String dni, // sin validar para pruebas
        String direccion,
        String telefono,
        @MinAge(18) String fechaNacimiento,
        String estadoValidacion) {

    public AdoptanteRequest(Adoptante t) {
        this(
                t.getUsuarioId(),
                "", // nombre
                "", // apellido
                "", // dni (fetch from PerfilLegal if needed)
                "", // direccion (fetch from PerfilLegal if needed)
                "", // telefono (fetch from PerfilLegal if needed)
                t.getFechaNacimiento(),
                t.getEstadoValidacion() != null ? t.getEstadoValidacion().name() : null);
    }
}
