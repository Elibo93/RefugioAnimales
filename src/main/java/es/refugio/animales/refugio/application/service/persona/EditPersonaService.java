package es.refugio.animales.refugio.application.service.persona;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.command.persona.EditPersonaCommand;
import es.refugio.animales.refugio.application.usecase.persona.EditPersonaUseCase;
import es.refugio.animales.refugio.domain.model.persona.Persona;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditPersonaService {
  private final EditPersonaUseCase editPersonaUseCase;

  public Persona update(EditPersonaCommand comando) {
    Persona persona = editPersonaUseCase.update(comando);
    return persona;
  }

}
