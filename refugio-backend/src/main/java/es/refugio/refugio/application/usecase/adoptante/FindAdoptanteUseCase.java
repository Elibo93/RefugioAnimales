package es.refugio.refugio.application.usecase.adoptante;

import java.util.List;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.error.AdoptanteNotFoundException;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public List<Adoptante> findAll() {
        List<Adoptante> adoptantes = adoptanteRepository.getAll();

        if (adoptantes.isEmpty()) {
            throw new AdoptanteNotFoundException();
        }
        return adoptantes;
    }

    public Adoptante findById(AdoptanteId id) {
        return adoptanteRepository.getById(id)
                .orElseThrow(() -> new AdoptanteNotFoundException(id.getValue()));
    }

    public Adoptante findByUsuarioId(UsuarioId id) {
        return adoptanteRepository.getByUsuarioId(id)
                .orElseThrow(AdoptanteNotFoundException::new);
    }
}