package es.refugio.refugio.application.usecase.tarea;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Objects;
import es.refugio.refugio.application.command.tarea.EditTareaCommand;
import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.error.TareaNotFoundException;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.tarea.event.TareaStatusChangedEvent;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditTareaUseCase {

    private final TareaRepository tareaRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final PerfilLegalRepository perfilLegalRepository;
    private final NotificacionService notificacionService;
    private final ApplicationEventPublisher eventPublisher;

    public Tarea update(EditTareaCommand command) {
        return tareaRepository.getById(command.id())
                .map(tarea -> {
                    EstadoTarea estadoAnterior = tarea.getEstado();
                    EstadoTarea estadoEnum = EstadoTarea.valueOf(command.estado().toUpperCase());

                    // Publicar evento de cambio de estado si ha cambiado
                    if (estadoEnum != estadoAnterior) {
                        eventPublisher.publishEvent(TareaStatusChangedEvent.builder()
                                .tareaId(tarea.getId())
                                .estadoAnterior(estadoAnterior)
                                .estadoNuevo(estadoEnum)
                                .usuarioActorId(command.voluntarioActorId())
                                .timestamp(LocalDateTime.now())
                                .voluntarioIds(command.voluntarioIds())
                                .build());
                    }

                    // Lógica de Notificaciones

                    // 1. Si la tarea se marca como PROPUESTA, notificar a los voluntarios
                    if (estadoEnum == EstadoTarea.PROPUESTA && tarea.getEstado() != EstadoTarea.PROPUESTA) {
                        if (command.voluntarioIds() != null) {
                            command.voluntarioIds().forEach(vId -> {
                                voluntarioRepository.getById(new VoluntarioId(vId)).ifPresent(vol -> {
                                    notificacionService.enviar(
                                            vol.getUsuarioId().getValue(),
                                            "Nueva Tarea Propuesta",
                                            "Se te ha propuesto una nueva tarea: '" + tarea.getDescripcion()
                                                    + "'. Por favor, acéptala o recházala.",
                                            "TAREA_PROPUESTA",
                                            "/web/tareas");
                                });
                            });
                        }
                    }

                    // 2. Si la tarea se marca como COMPLETADA, notificar a los Administradores
                    if ((estadoEnum == EstadoTarea.COMPLETADA || estadoEnum == EstadoTarea.FINALIZADA)
                            && tarea.getEstado() != EstadoTarea.COMPLETADA
                            && tarea.getEstado() != EstadoTarea.FINALIZADA) {
                        notificacionService.enviarARol(
                                "ROLE_ADMIN",
                                "Tarea Finalizada",
                                "Un voluntario ha marcado la tarea '" + tarea.getDescripcion() + "' como realizada.",
                                "TAREA",
                                "/web/tareas");
                    }

                    // 3. Si la tarea se marca como ACEPTADA o RECHAZADA, notificar a los
                    // Administradores
                    if ((estadoEnum == EstadoTarea.ACEPTADA || estadoEnum == EstadoTarea.RECHAZADA)
                            && tarea.getEstado() != estadoEnum) {

                        String volunteerName = "Un voluntario";
                        if (command.voluntarioActorId() != null) {
                            var volOpt = voluntarioRepository.getById(new VoluntarioId(command.voluntarioActorId()));
                            if (volOpt.isPresent()) {
                                var perfilOpt = perfilLegalRepository
                                        .findByUsuarioId(volOpt.get().getUsuarioId().getValue());
                                if (perfilOpt.isPresent()) {
                                    PerfilLegal p = perfilOpt.get();
                                    volunteerName = p.getNombre() + " " + p.getApellido();
                                }
                            }
                        }

                        String msg = estadoEnum == EstadoTarea.ACEPTADA
                                ? volunteerName + " ha ACEPTADO la tarea '" + tarea.getDescripcion() + "'."
                                : volunteerName + " ha RECHAZADO la tarea '" + tarea.getDescripcion() + "'.";

                        notificacionService.enviarARol(
                                "ROLE_ADMIN",
                                "Tarea " + (estadoEnum == EstadoTarea.ACEPTADA ? "Aceptada" : "Rechazada"),
                                msg,
                                "TAREA",
                                "/web/tareas");
                    }

                    // Resetear flag de notificación si cambia la fecha límite
                    if (command.fechaLimite() != null && !command.fechaLimite().equals(tarea.getFechaLimite())) {
                        tarea.setNotificadoVencimiento(false);
                    }

                    tarea.setDescripcion(command.descripcion());
                    tarea.setFecha(command.fecha());
                    tarea.setEstado(estadoEnum);
                    tarea.setFechaLimite(command.fechaLimite());
                    tarea.setInstrucciones(command.instrucciones());
                    tarea.setVoluntarios(command.voluntarioIds() != null ? command.voluntarioIds().stream()
                            .filter(Objects::nonNull)
                            .map(VoluntarioId::new)
                            .collect(Collectors.toList()) : null);

                    return tareaRepository.save(tarea);
                })
                .orElseThrow(() -> new TareaNotFoundException(command.id().getValue()));
    }
}
