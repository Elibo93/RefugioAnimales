package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.EditVoluntarioCommand;
import es.refugio.refugio.domain.error.VoluntarioNotFoundException;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditVoluntarioUseCase {

    // Atributos
    private final VoluntarioRepository voluntarioRepository;

    public Voluntario update(EditVoluntarioCommand command) {
        return voluntarioRepository.getById(command.id())
                .map(p -> { // ACtualizamos los atributos del objeto
                    p.setEspecialidad(command.especialidad());
                    p.setEmail(command.email());
                    p.setTelefono(command.telefono());
                    return voluntarioRepository.save(p);
                })
                .orElseThrow(() -> new VoluntarioNotFoundException(command.id().getValue()));
    }
}

















