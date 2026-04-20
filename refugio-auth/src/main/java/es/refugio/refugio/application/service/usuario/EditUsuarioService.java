package es.refugio.refugio.application.service.usuario;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.usecase.usuario.EditUsuarioUseCase;
import es.refugio.refugio.domain.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditUsuarioService {

    // Cambiamos editPersonaUseCase por editUsuarioUseCase
    private final EditUsuarioUseCase editUsuarioUseCase;

    public Usuario update(EditUsuarioCommand comando) {
        // Cambiamos la variable local persona por usuario
        return editUsuarioUseCase.update(comando);
    }
}