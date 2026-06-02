package es.refugio.refugio.application.usecase.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Adoptante.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public void delete(AdoptanteId id) {
        adoptanteRepository.deleteById(id);
    }
}