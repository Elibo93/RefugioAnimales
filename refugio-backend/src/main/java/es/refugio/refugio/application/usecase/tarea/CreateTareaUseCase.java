package es.refugio.refugio.application.usecase.tarea;

import java.util.stream.Collectors;
import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.TareaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTareaUseCase {

    private final TareaRepository tareaRepository;

    public Tarea create(CreateTareaCommand command) {
        EstadoTarea estadoEnum = EstadoTarea.valueOf(command.estado().toUpperCase());
        
        Tarea tarea = Tarea.builder()
                .descripcion(command.descripcion())
                .fecha(command.fecha())
                .estado(estadoEnum)
                .voluntarios(command.voluntarioIds() != null ? 
                    command.voluntarioIds().stream().map(VoluntarioId::new).collect(Collectors.toList()) : 
                    null)
                .build();
                
        return tareaRepository.save(tarea);
    }
}
