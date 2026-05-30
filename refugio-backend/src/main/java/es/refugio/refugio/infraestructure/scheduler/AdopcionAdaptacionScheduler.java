package es.refugio.refugio.infraestructure.scheduler;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdopcionAdaptacionScheduler {

    private final AdopcionRepository adopcionRepository;
    private final NotificacionService notificacionService;
    private final MessageSource messageSource;

    // Se ejecuta cada día a las 02:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void revisarPeriodosDeAdaptacion() {
        log.info("Iniciando revisión de periodos de adaptación de adopciones...");
        
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        
        List<Adopcion> adopcionesACompletar = adopcionRepository.findByEstadoAndFechaAdopcionBefore(
                EstadoAdopcion.EN_PERIODO_ADAPTACION, 
                hace30Dias
        );
        
        if (adopcionesACompletar.isEmpty()) {
            log.info("No hay adopciones que hayan superado los 30 días de adaptación.");
            return;
        }

        log.info("Se encontraron {} adopciones que superaron el periodo de adaptación.", adopcionesACompletar.size());

        for (Adopcion adopcion : adopcionesACompletar) {
            String titulo = messageSource.getMessage("notificacion.adopcion.pendiente.titulo", null, Locale.getDefault());
            String mensaje = messageSource.getMessage("notificacion.adopcion.pendiente.mensaje", new Object[]{adopcion.getId().getValue()}, Locale.getDefault());
            String enlace = "/web/adopciones";
            
            notificacionService.enviarARol("ROLE_ADMIN", titulo, mensaje, "SISTEMA", enlace);
            log.info("Notificación enviada a ADMIN para la adopción {}", adopcion.getId().getValue());
        }
    }
}
