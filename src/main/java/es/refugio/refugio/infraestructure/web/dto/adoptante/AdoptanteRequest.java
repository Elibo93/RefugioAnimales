package es.refugio.refugio.infraestructure.web.dto.adoptante;

import es.refugio.refugio.domain.model.adoptante.Adoptante;

public record AdoptanteRequest(
        Integer usuarioId,
        String dni,
        String direccion,
        String estadoValidacion) {

    // Constructor de conveniencia para crear un Request a partir del Dominio
    public AdoptanteRequest(Adoptante t) {
        this(
                t.getUsuarioId(),
                t.getDni(),
                t.getDireccion(),
                t.getEstadoValidacion());
    }
}