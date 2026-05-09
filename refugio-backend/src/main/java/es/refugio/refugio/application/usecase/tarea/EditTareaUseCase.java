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
    private final es.refugio.refugio.application.service.NotificacionService notificacionService;

    public Tarea update(EditTareaCommand command) {
        return tareaRepository.getById(command.id())
                .map(tarea -> {
                    EstadoTarea estadoEnum = EstadoTarea.valueOf(command.estado().toUpperCase());
                    
                    // Si la tarea se marca como COMPLETADA, notificar a los Administradores
                    if (estadoEnum == EstadoTarea.COMPLETADA && tarea.getEstado() != EstadoTarea.COMPLETADA) {
                        notificacionService.enviarARol(
                            "ROLE_ADMIN",
                            "Tarea Finalizada",
                            "Un voluntario ha marcado la tarea '" + tarea.getDescripcion() + "' como realizada.",
                            "TAREA",
                            "/web/tareas"
                        );
                    }

                    tarea.setDescripcion(command.descripcion());
                    tarea.setFecha(command.fecha());
                    tarea.setEstado(estadoEnum);
                    tarea.setFechaLimite(command.fechaLimite());
                    tarea.setInstrucciones(command.instrucciones());
                    tarea.setVoluntarios(command.voluntarioIds() != null ? 
                        command.voluntarioIds().stream().map(VoluntarioId::new).collect(Collectors.toList()) : 
                        null);
                    
                    return tareaRepository.save(tarea);
                })
                .orElseThrow(() -> new TareaNotFoundException(command.id().getValue()));
    }
}
