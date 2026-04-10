package es.refugio.refugio.application.usecase.historial_medico;

import es.refugio.refugio.application.command.historial_medico.CreateHistorialMedicoCommand;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedico;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateHistorialMedicoUseCase {

    private final HistorialMedicoRepository historialMedicoRepository;

    public HistorialMedico create(CreateHistorialMedicoCommand command) {
        HistorialMedico historialMedico = HistorialMedico.builder()
                .animalId(new AnimalId(command.animalId()))
                .fecha(command.fecha())
                .descripcion(command.descripcion())
                .tratamiento(command.tratamiento())
                .veterinario(command.veterinario())
                .build();
        return historialMedicoRepository.save(historialMedico);
    }
}
