package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.domain.error.VoluntarioNotFoundException;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Edit Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;

    public Voluntario update(EditVoluntarioCommand command) {
        return voluntarioRepository.getById(command.id())
                .map(voluntario -> {
                    voluntario.setDisponibilidad(command.disponibilidad());
                    voluntario.setEspecialidad(command.especialidad());
                    return voluntarioRepository.save(voluntario);
                })
                .orElseThrow(() -> new VoluntarioNotFoundException(command.id().getValue()));
    }
}
