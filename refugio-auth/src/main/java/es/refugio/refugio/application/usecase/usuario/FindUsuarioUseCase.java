package es.refugio.refugio.application.usecase.usuario;

import java.util.List;

import es.refugio.refugio.domain.error.UsuarioNotFoundException;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.getAll();
    }

    public Usuario findById(UsuarioId id) {
        return usuarioRepository.getById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id.getValue()));
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.getByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
}