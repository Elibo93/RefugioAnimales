package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;

    public Voluntario create(CreateVoluntarioCommand command) {
        Voluntario voluntario = Voluntario.builder()
                .usuarioId(new UsuarioId(command.usuarioId()))
                .disponibilidad(command.disponibilidad())
                .build();
                
        return voluntarioRepository.save(voluntario);
    }
}
