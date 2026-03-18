package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteAdopcionUseCase {
    public final AdopcionRepository adopcionRepository;

    public void delete(AdopcionId id) {
        adopcionRepository.deleteById(id);
    }

}



















