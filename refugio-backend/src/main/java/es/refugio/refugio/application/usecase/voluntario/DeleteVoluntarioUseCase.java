package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;

    public void delete(VoluntarioId id) {
        voluntarioRepository.deleteById(id);
    }
}
