package es.refugio.animales.refugio.application.service.persona;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.command.persona.CreatePersonaCommand;
import es.refugio.animales.refugio.application.usecase.persona.CreatePersonaUseCase;
import es.refugio.animales.refugio.domain.model.persona.Persona;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreatePersonaService {

    private final CreatePersonaUseCase createPersonaUseCase;

    public Persona createPersona(CreatePersonaCommand comando) {
        Persona persona = createPersonaUseCase.create(comando);
        return persona;

    }

}
