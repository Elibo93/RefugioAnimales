package es.refugio.refugio.application.command.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;

/**
 * Command para rechazar un adoptante.
 * Solo requiere el ID — el estado se fija a "RECHAZADO" en el UseCase.
 */
public record RejectAdoptanteCommand(AdoptanteId id) {}
