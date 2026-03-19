package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;

    public void delete(VoluntarioId id) {
        voluntarioRepository.deleteById(id);
    }
}
