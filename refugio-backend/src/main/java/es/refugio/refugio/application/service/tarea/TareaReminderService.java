package es.refugio.refugio.application.service.tarea;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaEntityJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareaReminderService {

    private final TareaEntityJpaRepository tareaRepository;
    private final NotificacionService notificacionService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Se ejecuta cada hora (3600000 ms) para buscar tareas próximas a vencer.
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void verificarVencimientos() {
        log.info("Iniciando verificación de vencimientos de tareas...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusHours(24);

        // Solo notificamos tareas pendientes, aceptadas o en proceso que venzan en las próximas 24h
        List<EstadoTarea> estadosActivos = List.of(EstadoTarea.PENDIENTE, EstadoTarea.ACEPTADA, EstadoTarea.EN_PROCESO);
        
        List<TareaEntity> proximasAVencer = tareaRepository.findByFechaLimiteBetweenAndNotificadoVencimientoFalseAndEstadoIn(
            now, threshold, estadosActivos
        );

        log.info("Se han encontrado {} tareas próximas a vencer.", proximasAVencer.size());

        for (TareaEntity tarea : proximasAVencer) {
            notificarVoluntarios(tarea);
            tarea.setNotificadoVencimiento(true);
            tareaRepository.save(tarea);
        }
    }

    private void notificarVoluntarios(TareaEntity tarea) {
        if (tarea.getVoluntarios() == null) return;

        for (VoluntarioEntity voluntario : tarea.getVoluntarios()) {
            String titulo = "⚠️ Tarea próxima a vencer";
            String mensaje = String.format("La tarea '%s' vence el %s. ¡No la olvides!", 
                tarea.getDescripcion(), 
                tarea.getFechaLimite().format(DATE_FORMATTER));
            
            notificacionService.enviar(
                voluntario.getUsuarioId(), 
                titulo, 
                mensaje, 
                "URGENTE", 
                "/web/tareas"
            );
            
            log.info("Notificación de vencimiento enviada a voluntario {} para tarea {}", 
                voluntario.getUsuarioId(), tarea.getId());
        }
    }
}
