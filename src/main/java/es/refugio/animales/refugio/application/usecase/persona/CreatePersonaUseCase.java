package es.refugio.animales.refugio.application.usecase.persona;

import java.time.LocalDateTime;

import es.refugio.animales.refugio.application.command.usuario.CreatePersonaCommand;
import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreatePersonaUseCase {
    private final PersonaRepository personaRepository;

    public Persona create(CreatePersonaCommand comando) {
        Persona persona = Persona.builder()
                .dni(comando.dni())
                .nombre(comando.nombre())
                .apellido(comando.apellido())
                .email(comando.email())
                .telefono(comando.telefono())
                .direccion(comando.direccion())
                .fechaNacimiento(comando.fechaNacimiento())
                .createdAt(LocalDateTime.now())
                .build();
        return personaRepository.save(persona);

    }

}
