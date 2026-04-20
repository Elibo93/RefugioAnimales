package es.refugio.refugio.application.usecase.adoptante;

import es.refugio.refugio.application.command.adoptante.RejectAdoptanteCommand;
import es.refugio.refugio.domain.error.AdoptanteNotFoundException;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import lombok.AllArgsConstructor;

/**
 * Caso de uso: rechaza un adoptante cambiando su estado a RECHAZADO.
 */
@AllArgsConstructor
public class RejectAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public Adoptante reject(RejectAdoptanteCommand command) {
        return adoptanteRepository.getById(command.id())
                .map(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.RECHAZADO);
                    return adoptanteRepository.save(adoptante);
                })
                .orElseThrow(() -> new AdoptanteNotFoundException(command.id().getValue()));
    }
}
