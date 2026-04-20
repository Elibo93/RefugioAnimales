package es.refugio.refugio.application.usecase.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public void delete(AdoptanteId id) {
        adoptanteRepository.deleteById(id);
    }
}