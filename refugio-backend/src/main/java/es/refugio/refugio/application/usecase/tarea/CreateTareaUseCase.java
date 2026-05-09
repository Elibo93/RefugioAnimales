package es.refugio.refugio.application.usecase.tarea;

import java.util.stream.Collectors;
import es.refugio.refugio.application.command.tarea.CreateTareaCommand;
import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.application.service.NotificacionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTareaUseCase {

    private final TareaRepository tareaRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final NotificacionService notificacionService;

    public Tarea create(CreateTareaCommand command) {
        EstadoTarea estadoEnum = EstadoTarea.valueOf(command.estado().toUpperCase());
        
        Tarea tarea = Tarea.builder()
                .descripcion(command.descripcion())
                .fecha(command.fecha())
                .estado(estadoEnum)
                .fechaLimite(command.fechaLimite())
                .instrucciones(command.instrucciones())
                .voluntarios(command.voluntarioIds() != null ? 
                    command.voluntarioIds().stream().map(VoluntarioId::new).collect(Collectors.toList()) : 
                    null)
                .build();
                
        Tarea saved = tareaRepository.save(tarea);

        // Enviar notificaciones a los voluntarios asignados
        if (command.voluntarioIds() != null) {
            System.out.println("DEBUG TAREA: Notificando a " + command.voluntarioIds().size() + " voluntarios.");
            command.voluntarioIds().forEach(vIdInt -> {
                VoluntarioId volId = new VoluntarioId(vIdInt);
                voluntarioRepository.getById(volId).ifPresentOrElse(vol -> {
                    if (vol.getUsuarioId() != null) {
                        System.out.println("DEBUG TAREA: Enviando notificación a UsuarioID=" + vol.getUsuarioId().getValue());
                        notificacionService.enviar(
                            vol.getUsuarioId().getValue(),
                            "Nueva Tarea Asignada",
                            "Se te ha asignado la tarea: " + saved.getDescripcion(),
                            "TAREA",
                            "/web/tareas"
                        );
                    } else {
                        System.out.println("DEBUG TAREA: El voluntario " + vIdInt + " no tiene UsuarioID asociado.");
                    }
                }, () -> {
                    System.out.println("DEBUG TAREA: No se encontró el voluntario con ID=" + vIdInt);
                });
            });
        } else {
            System.out.println("DEBUG TAREA: No hay voluntarios para notificar.");
        }

        return saved;
    }
}
