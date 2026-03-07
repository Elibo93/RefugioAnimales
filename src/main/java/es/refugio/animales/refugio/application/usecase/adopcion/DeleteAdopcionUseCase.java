package es.refugio.animales.refugio.application.usecase.adopcion;

import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.animales.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteAdopcionUseCase {
    public final AdopcionRepository adopcionRepository;

    public void delete(AdopcionId id) {
        adopcionRepository.deleteById(id);
    }

}



















