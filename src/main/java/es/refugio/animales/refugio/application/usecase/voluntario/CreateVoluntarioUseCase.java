package es.refugio.animales.refugio.application.usecase.voluntario;

import java.time.LocalDateTime;

import es.refugio.animales.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import es.refugio.animales.refugio.domain.repository.VoluntarioRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateVoluntarioUseCase {

    // Atributos
    private final VoluntarioRepository voluntarioRepository;

    // Metodo para crear un Voluntario
    public Voluntario create(CreateVoluntarioCommand comando) {

        Voluntario voluntario = Voluntario.builder()
                .nombre(comando.nombre())
                .apellido(comando.apellido())
                .especialidad(comando.especialidad())
                .email(comando.email())
                .telefono(comando.telefono())
                .createdAt(LocalDateTime.now()).build();

        return voluntarioRepository.save(voluntario);

    }
}

















