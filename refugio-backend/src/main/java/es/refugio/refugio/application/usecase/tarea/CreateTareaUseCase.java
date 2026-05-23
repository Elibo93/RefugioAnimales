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
import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.application.service.NotificacionService;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Create Tarea.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateTareaUseCase {

    private final TareaRepository tareaRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final NotificacionService notificacionService;
    private final ApplicationEventPublisher eventPublisher;

    public Tarea create(CreateTareaCommand command) {
        if (command.fechaLimite() != null && command.fechaLimite().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("error.tarea.fecha_limite_pasada");
        }
        
        if (command.voluntarioIds() != null && command.fechaLimite() != null) {
            java.time.LocalDate limitDate = command.fechaLimite().toLocalDate();
            for (Integer vIdInt : command.voluntarioIds()) {
                if (vIdInt == null) continue;
                VoluntarioId volId = new VoluntarioId(vIdInt);
                voluntarioRepository.getById(volId).ifPresent(vol -> {
                    if (vol.getDisponibilidades() != null) {
                        boolean noDisponible = vol.getDisponibilidades().stream()
                                .anyMatch(d -> d.getFecha().equals(limitDate) 
                                            && d.getEstado() == EstadoDisponibilidad.NO_DISPONIBLE);
                        if (noDisponible) {
                            throw new IllegalArgumentException("error.tarea.voluntario_no_disponible");
                        }
                    }
                });
            }
        }

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
                .usuarioActorId(command.voluntarioActorId() != null ? command.voluntarioActorId() : 1) // Usar actor provisto o fallback a 1
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
