package es.refugio.refugio.infraestructure.web.dto.adoptante;

import es.refugio.refugio.domain.model.adoptante.Adoptante;

public record AdoptanteRequest(
        Integer usuarioId,
        String estadoValidacion) {

    public AdoptanteRequest(Adoptante t) {
        this(
                t.getUsuarioId(),
                t.getEstadoValidacion() != null ? t.getEstadoValidacion().name() : null);
    }
}
