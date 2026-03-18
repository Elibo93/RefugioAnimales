package es.refugio.refugio.application.service.usuario;

import java.util.List;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.usecase.usuario.FindUsuarioUseCase;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindUsuarioService {

    private final FindUsuarioUseCase findPersonaUseCase;

    public List<Usuario> findAll() {
        return findPersonaUseCase.findAll();
    }

    public Usuario findById(UsuarioId id) {
        return findPersonaUseCase.findById(id);
    }
}
