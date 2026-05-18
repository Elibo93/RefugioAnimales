package es.refugio.refugio.application.usecase.tarea;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Objects;
import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.tarea.event.TareaStatusChangedEvent;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.application.service.NotificacionService;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTareaUseCase {

    private final TareaRepository tareaRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final NotificacionService notificacionService;
    private final ApplicationEventPublisher eventPublisher;

    public Tarea create(CreateTareaCommand command) {
        EstadoTarea estadoEnum = EstadoTarea.valueOf(command.estado().toUpperCase());
        EstadoTarea estadoFinal = command.voluntarioIds() != null && !command.voluntarioIds().isEmpty() ? EstadoTarea.PROPUESTA : estadoEnum;
        
        Tarea tarea = Tarea.builder()
                .descripcion(command.descripcion())
                .fecha(command.fecha())
                .estado(estadoFinal)
                .fechaLimite(command.fechaLimite())
                .instrucciones(command.instrucciones())
                .voluntarios(command.voluntarioIds() != null ? 
                    command.voluntarioIds().stream()
                        .filter(Objects::nonNull)
                        .map(VoluntarioId::new)
                        .collect(Collectors.toList()) : 
                    null)
                .build();
                
        Tarea saved = tareaRepository.save(tarea);

        // Registrar en el historial la creación
        eventPublisher.publishEvent(TareaStatusChangedEvent.builder()
                .tareaId(saved.getId())
                .estadoAnterior(null) // Es creación
                .estadoNuevo(saved.getEstado())
                .usuarioActorId(1) // Placeholder: Administrador por defecto. Idealmente vendría del comando.
                .timestamp(LocalDateTime.now())
                .observaciones("Creación de la tarea")
                .voluntarioIds(command.voluntarioIds())
                .build());

        // Enviar notificaciones a los voluntarios asignados
        if (command.voluntarioIds() != null) {
            command.voluntarioIds().forEach(vIdInt -> {
                VoluntarioId volId = new VoluntarioId(vIdInt);
                voluntarioRepository.getById(volId).ifPresent(vol -> {
                    if (vol.getUsuarioId() != null) {
                        if (saved.getEstado() == EstadoTarea.PROPUESTA) {
                            notificacionService.enviar(
                                vol.getUsuarioId().getValue(),
                                "Nueva Tarea Propuesta",
                                "Se te ha propuesto una nueva tarea: '" + saved.getDescripcion() + "'. Por favor, acéptala o recházala.",
                                "TAREA_PROPUESTA",
                                "/web/tareas"
                            );
                        } else {
                            notificacionService.enviar(
                                vol.getUsuarioId().getValue(),
                                "Nueva Tarea Asignada",
                                "Se te ha asignado la tarea: " + saved.getDescripcion(),
                                "TAREA",
                                "/web/tareas"
                            );
                        }
                    }
                });
            });
        }

        return saved;
    }
}
