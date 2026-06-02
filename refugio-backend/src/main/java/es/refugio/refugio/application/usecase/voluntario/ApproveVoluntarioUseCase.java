package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.domain.error.VoluntarioNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Approve Voluntario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class ApproveVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;
    private final NotificacionService notificacionService;
    private final RestTemplate restTemplate;

    // URL del servicio de Auth (via Eureka)
    private static final String AUTH_SERVICE_URL = "http://REFUGIO-AUTH/api/v1/usuarios/";

    public void approve(VoluntarioId id, String adminToken) {
        Voluntario voluntario = voluntarioRepository.getById(id)
                .orElseThrow(() -> new VoluntarioNotFoundException());

        if (voluntario.getEstado() != EstadoVoluntario.PENDIENTE) {
            throw new IllegalStateException("error.voluntario.solo_pendientes_aprobar");
        }

        // 1. Obtener datos del usuario desde Auth Service para saber su rol actual
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                AUTH_SERVICE_URL + voluntario.getUsuarioId().getValue(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new RuntimeException("Error al consultar datos del usuario en el servicio de Auth");
        }

        String currentRol = (String) userResponse.getBody().get("rol");
        String newRol = "ROLE_VOLUNTARIO";

        if ("ROLE_ADMIN".equals(currentRol)) {
            newRol = "ROLE_ADMIN"; // Mantener admin si ya lo es
        } else if ("ROLE_ADOPTANTE".equals(currentRol)) {
            newRol = "ROLE_VOLUNTARIO_ADOPTANTE";
        }

        // 2. Actualizar Rol en Auth Service
        Map<String, String> roleBody = Map.of("rol", newRol);
        HttpEntity<Map<String, String>> roleRequest = new HttpEntity<>(roleBody, headers);

        restTemplate.exchange(
                AUTH_SERVICE_URL + voluntario.getUsuarioId().getValue() + "/rol",
                HttpMethod.PUT,
                roleRequest,
                Map.class
        );

        // 3. Actualizar Estado en Backend
        voluntario.setEstado(EstadoVoluntario.APROBADO);
        voluntarioRepository.save(voluntario);

        // 4. Notificar al usuario
        notificacionService.enviar(
                voluntario.getUsuarioId().getValue(),
                "¡Solicitud Aprobada!",
                "Has sido aceptado como voluntario. Ya puedes acceder a la gestión de tareas.",
                "SISTEMA",
                "/web/tareas"
        );

        notificacionService.enviar(
                voluntario.getUsuarioId().getValue(),
                "Configura tu Disponibilidad",
                "Es necesario que indiques qué días estarás disponible en tu calendario para poder asignarte tareas operativas.",
                "SISTEMA",
                "/web/personas/" + voluntario.getUsuarioId().getValue()
        );
    }

    public void reject(VoluntarioId id, String adminToken) {
        Voluntario voluntario = voluntarioRepository.getById(id)
                .orElseThrow(() -> new VoluntarioNotFoundException());

        if (voluntario.getEstado() != EstadoVoluntario.PENDIENTE) {
            throw new IllegalStateException("error.voluntario.solo_pendientes_rechazar");
        }

        voluntario.setEstado(EstadoVoluntario.RECHAZADO);
        voluntarioRepository.save(voluntario);

        notificacionService.enviar(
                voluntario.getUsuarioId().getValue(),
                "Solicitud de Voluntariado",
                "Lo sentimos, tu solicitud ha sido rechazada por el momento.",
                "SISTEMA",
                "/web/home"
        );
    }
}
