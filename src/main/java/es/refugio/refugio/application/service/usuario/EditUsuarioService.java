package es.refugio.refugio.application.service.usuario;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.usecase.usuario.EditUsuarioUseCase;
import es.refugio.refugio.domain.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditUsuarioService {
  private final EditUsuarioUseCase editPersonaUseCase;

  public Usuario update(EditUsuarioCommand comando) {
    Usuario persona = editPersonaUseCase.update(comando);
    return persona;
  }

}
