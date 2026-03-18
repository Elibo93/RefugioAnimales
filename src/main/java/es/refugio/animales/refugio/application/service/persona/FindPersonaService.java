package es.refugio.animales.refugio.application.service.persona;

import java.util.List;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.usecase.persona.FindPersonaUseCase;
import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindPersonaService {

    private final FindPersonaUseCase findPersonaUseCase;

    public List<Persona> findAll() {
        return findPersonaUseCase.findAll();
    }

    public Persona findById(PersonaId id) {
        return findPersonaUseCase.findById(id);
    }
}
