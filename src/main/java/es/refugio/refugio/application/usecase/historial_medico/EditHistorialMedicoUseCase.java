package es.refugio.refugio.application.usecase.historial_medico;

import es.refugio.refugio.application.command.historial_medico.EditHistorialMedicoCommand;
import es.refugio.refugio.domain.error.HistorialMedicoNotFoundException;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditHistorialMedicoUseCase {

    private final HistorialMedicoRepository historialMedicoRepository;

    public HistorialMedico update(EditHistorialMedicoCommand command) {
        return historialMedicoRepository.getById(command.id())
                .map(historialMedico -> {
                    historialMedico.setAnimalId(new AnimalId(command.animalId()));
                    historialMedico.setFecha(command.fecha());
                    historialMedico.setDescripcion(command.descripcion());
                    historialMedico.setTratamiento(command.tratamiento());
                    historialMedico.setVeterinario(command.veterinario());
                    return historialMedicoRepository.save(historialMedico);
                })
                .orElseThrow(() -> new HistorialMedicoNotFoundException(command.id().getValue()));
    }
}
