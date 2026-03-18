package es.refugio.refugio.application.usecase.adoptante;

import es.refugio.refugio.application.command.adoptante.EditAdoptanteCommand;
import es.refugio.refugio.domain.error.AdoptanteNotFoundException;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditAdoptanteUseCase {

    private final AdoptanteRepository adoptanteRepository;

    public Adoptante update(EditAdoptanteCommand command) {
        return adoptanteRepository.getById(command.id())
                .map(t -> {
                    t.setDni(command.dni());
                    t.setDireccion(command.direccion());
                    t.setEstadoValidacion(command.estadoValidacion());
                    // El usuarioId no se suele cambiar una vez creado, 
                    // por eso no lo incluimos aquí.
                    return adoptanteRepository.save(t);
                })
                .orElseThrow(() -> new AdoptanteNotFoundException(command.id().getValue()));
    }
}