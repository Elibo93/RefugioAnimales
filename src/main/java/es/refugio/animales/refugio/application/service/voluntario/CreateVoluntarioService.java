package es.refugio.animales.refugio.application.service.voluntario;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.animales.refugio.application.usecase.voluntario.CreateVoluntarioUseCase;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateVoluntarioService {

    // Atributos
    private final CreateVoluntarioUseCase createVoluntarioUseCase;

    public Voluntario createVoluntario(CreateVoluntarioCommand command) {
        Voluntario voluntario = createVoluntarioUseCase.create(command);
        return voluntario;
    }
}
















