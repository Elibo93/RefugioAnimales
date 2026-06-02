package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;

    public void delete(AdopcionId id) {
        adopcionRepository.deleteById(id);
    }
}
