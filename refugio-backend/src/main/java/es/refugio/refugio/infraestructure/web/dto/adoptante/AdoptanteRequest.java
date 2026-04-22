package es.refugio.refugio.infraestructure.web.dto.adoptante;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.infraestructure.web.validation.ValidDni;
import es.refugio.refugio.infraestructure.web.validation.MinAge;

public record AdoptanteRequest(
        Integer usuarioId,
        @ValidDni String dni,
        String direccion,
        @MinAge(18) String fechaNacimiento,
        String estadoValidacion) {

    public AdoptanteRequest(Adoptante t) {
        this(
                t.getUsuarioId(),
                t.getDni(),
                t.getDireccion(),
                t.getFechaNacimiento(),
                t.getEstadoValidacion() != null ? t.getEstadoValidacion().name() : null);
    }
}
