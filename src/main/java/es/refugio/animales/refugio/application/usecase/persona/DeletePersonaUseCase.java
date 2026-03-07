package es.refugio.animales.refugio.application.usecase.persona;

import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeletePersonaUseCase {
    public final PersonaRepository personaRepository;

    public void delete(PersonaId id) {
        personaRepository.deleteById(id);
    }

}
