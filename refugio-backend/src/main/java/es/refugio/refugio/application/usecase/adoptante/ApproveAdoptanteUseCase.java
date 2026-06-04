package es.refugio.refugio.application.usecase.adoptante;

import es.refugio.refugio.application.command.adoptante.ApproveAdoptanteCommand;
import es.refugio.refugio.domain.error.AdoptanteNotFoundException;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import lombok.AllArgsConstructor;

/**
 * Caso de uso: aprueba un adoptante cambiando su estado a APROBADO.
 */
@AllArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Approve Adoptante.
 *
 * @author Elisabeth
 * @author Diego
 */
public class ApproveAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public Adoptante approve(ApproveAdoptanteCommand command) {
        return adoptanteRepository.getById(command.id())
                .map(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.APROBADO);
                    return adoptanteRepository.save(adoptante);
                })
                .orElseThrow(() -> new AdoptanteNotFoundException(command.id().getValue()));
    }
}
