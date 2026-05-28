package es.refugio.frontend.service;

import es.refugio.frontend.client.BackendFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Notificacion.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final BackendFeignClient backendClient;

    public List<Map<String, Object>> fetchMisNotificaciones() {
        try {
            return backendClient.getMisNotificaciones();
        } catch (Exception e) {
            return List.of();
        }
    }

    public void marcarComoLeida(Integer id) {
        backendClient.marcarNotificacionLeida(id);
    }

    public Long contarNoLeidas() {
        try {
            return backendClient.countNotificacionesNoLeidas();
        } catch (Exception e) {
            return 0L;
        }
    }

    public void eliminarNotificacion(Integer id) {
        backendClient.deleteNotificacion(id);
    }
}
