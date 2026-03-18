package es.refugio.animales.refugio.application.usecase.persona;

import es.refugio.animales.refugio.application.command.usuario.EditPersonaCommand;
import es.refugio.animales.refugio.domain.error.PersonaNotFoundException;
import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditPersonaUseCase {
    private final PersonaRepository personaRepository;

    public Persona update(EditPersonaCommand command) {
        return personaRepository.getById(command.id())
                .map(p -> { // Actualizamos los atributos del objeto

                    p.setEmail(command.email());
                    return personaRepository.save(p);
                })
                .orElseThrow(() -> new PersonaNotFoundException(command.id().getValue())); // Lo cambiamos

    }

}
