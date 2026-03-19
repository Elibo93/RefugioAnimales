package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.domain.error.VoluntarioNotFoundException;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;

    public Voluntario update(EditVoluntarioCommand command) {
        return voluntarioRepository.getById(command.id())
                .map(voluntario -> {
                    voluntario.setUsuarioId(new UsuarioId(command.usuarioId()));
                    voluntario.setDisponibilidad(command.disponibilidad());
                    return voluntarioRepository.save(voluntario);
                })
                .orElseThrow(() -> new VoluntarioNotFoundException(command.id().getValue()));
    }
}
