package es.refugio.refugio.infraestructure.web.dto.adoptante;

import es.refugio.refugio.domain.model.adoptante.Adoptante;

public record AdoptanteRequest(
        Integer usuarioId,
        String dni,
        String direccion,
        String fechaNacimiento,
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