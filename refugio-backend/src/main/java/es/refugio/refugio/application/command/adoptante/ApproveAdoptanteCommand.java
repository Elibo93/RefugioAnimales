package es.refugio.refugio.application.command.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;

/**
 * Command para aprobar un adoptante.
 * Solo requiere el ID — el estado se fija a "APROBADO" en el UseCase.
 */
public record ApproveAdoptanteCommand(AdoptanteId id) {}
