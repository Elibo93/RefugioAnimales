package es.refugio.animales.refugio.application.usecase.voluntario;

import es.refugio.animales.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.animales.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteVoluntarioUseCase {

    // Atributos
    private final VoluntarioRepository voluntarioRepository;

    public void delete(VoluntarioId id) {
        voluntarioRepository.deleteById(id);
    }

}

















