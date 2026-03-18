package es.refugio.refugio.application.usecase.usuario;

import java.util.List;

import es.refugio.refugio.domain.error.UsuarioNotFoundException;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindUsuarioUseCase {
    private final UsuarioRepository personaRepository;

    public List<Usuario> findAll() {
        List<Usuario> personas = personaRepository.getAll();

        if (personas.isEmpty())
            throw new UsuarioNotFoundException();

        return personas;
    }

    public Usuario findById(UsuarioId id) {
        return personaRepository.getById(id).orElseThrow(() -> new UsuarioNotFoundException());
    }

}
