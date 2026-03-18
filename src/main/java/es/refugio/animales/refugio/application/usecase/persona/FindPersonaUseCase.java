package es.refugio.animales.refugio.application.usecase.persona;

import java.util.List;

import es.refugio.animales.refugio.domain.error.PersonaNotFoundException;
import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindPersonaUseCase {
    private final PersonaRepository personaRepository;

    public List<Persona> findAll() {
        List<Persona> personas = personaRepository.getAll();

        if (personas.isEmpty())
            throw new PersonaNotFoundException();

        return personas;
    }

    public Persona findById(PersonaId id) {
        return personaRepository.getById(id).orElseThrow(() -> new PersonaNotFoundException());
    }

}
