package es.refugio.refugio.application.usecase.tarea;

import java.util.stream.Collectors;
import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.domain.error.TareaNotFoundException;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.TareaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditTareaUseCase {

    private final TareaRepository tareaRepository;

    public Tarea update(EditTareaCommand command) {
        return tareaRepository.getById(command.id())
                .map(tarea -> {
                    EstadoTarea estadoEnum = EstadoTarea.valueOf(command.estado().toUpperCase());
                    
                    tarea.setDescripcion(command.descripcion());
                    tarea.setFecha(command.fecha());
                    tarea.setEstado(estadoEnum);
                    tarea.setVoluntarios(command.voluntarioIds() != null ? 
                        command.voluntarioIds().stream().map(VoluntarioId::new).collect(Collectors.toList()) : 
                        null);
                    
                    return tareaRepository.save(tarea);
                })
                .orElseThrow(() -> new TareaNotFoundException(command.id().getValue()));
    }
}
