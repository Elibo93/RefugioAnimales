package es.refugio.frontend.service;

import es.refugio.frontend.client.BackendFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Preferencia.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class PreferenciaService {

    private final BackendFeignClient backendClient;

    public void guardarPreferencias(Map<String, Object> payload) {
        backendClient.guardarPreferencias(payload);
    }
}
