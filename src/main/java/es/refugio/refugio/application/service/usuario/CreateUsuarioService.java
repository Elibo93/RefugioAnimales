package es.refugio.refugio.application.service.usuario;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.application.usecase.usuario.CreateUsuarioUseCase;
import es.refugio.refugio.domain.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateUsuarioService {

    private final CreateUsuarioUseCase createPersonaUseCase;

    public Usuario createPersona(CreateUsuarioCommand comando) {
        Usuario persona = createPersonaUseCase.create(comando);
        return persona;

    }

}
