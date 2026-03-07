package es.refugio.animales.refugio.application.service.persona;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.usecase.persona.DeletePersonaUseCase;
import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DeletePersonaService {

    private final DeletePersonaUseCase deletePersonaUseCase;

    public void delete(PersonaId id) {
        deletePersonaUseCase.delete(id);
    }

}
