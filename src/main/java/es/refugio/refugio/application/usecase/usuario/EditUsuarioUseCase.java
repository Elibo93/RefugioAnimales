package es.refugio.refugio.application.usecase.usuario;

import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.domain.error.UsuarioNotFoundException;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditUsuarioUseCase {
    private final UsuarioRepository personaRepository;

    public Usuario update(EditUsuarioCommand command) {
        return personaRepository.getById(command.id())
                .map(p -> { // Actualizamos los atributos del objeto

                    p.setEmail(command.email());
                    return personaRepository.save(p);
                })
                .orElseThrow(() -> new UsuarioNotFoundException(command.id().getValue())); // Lo cambiamos

    }

}
