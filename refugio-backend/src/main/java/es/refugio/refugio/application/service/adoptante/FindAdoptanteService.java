package es.refugio.refugio.application.service.adoptante;

import java.util.List;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.usecase.adoptante.FindAdoptanteUseCase;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindAdoptanteService {

    private final FindAdoptanteUseCase findAdoptanteUseCase;

    public List<Adoptante> findAll() {
        // Recupera todos los adoptantes (útil para el panel de administración)
        return findAdoptanteUseCase.findAll();
    }

    public Adoptante findById(AdoptanteId id) {
        // Busca un adoptante específico por su ID de dominio
        return findAdoptanteUseCase.findById(id);
    }

    public Adoptante findByUsuarioId(Integer usuarioId) {
        return findAdoptanteUseCase.findByUsuarioId(new UsuarioId(usuarioId));
    }
}