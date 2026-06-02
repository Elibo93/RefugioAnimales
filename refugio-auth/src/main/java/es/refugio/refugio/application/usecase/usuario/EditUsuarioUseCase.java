package es.refugio.refugio.application.usecase.usuario;

import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.domain.error.UsuarioNotFoundException;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditUsuarioUseCase {
    
    private final UsuarioRepository usuarioRepository;

    public Usuario update(EditUsuarioCommand command) {
        return usuarioRepository.getById(command.id())
                .map(u -> { 
                    u.setEmail(command.email());
                    u.setUsername(command.username());
                    u.setRol(command.rol());
                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new UsuarioNotFoundException(command.id().getValue()));
    }
}