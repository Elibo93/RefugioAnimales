package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * Caso de uso que encapsula la creación de un nuevo voluntario en el sistema.
 * Garantiza idempotencia (no crea un segundo voluntario si ya existe para ese usuario),
 * verifica que el usuario tenga un perfil legal completo y envía notificaciones
 * tanto al voluntario como a los administradores.
 *
 * @author Elisabeth
 * @author Diego
 */
@RequiredArgsConstructor
public class CreateVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;
    private final PerfilLegalRepository perfilLegalRepository;
    private final NotificacionService notificacionService;
    private final RestTemplate restTemplate;

    // URL del servicio de Auth (via Eureka)
    private static final String AUTH_SERVICE_URL = "http://REFUGIO-AUTH/api/v1/usuarios/";

    /**
     * Registra un nuevo voluntario en el sistema para el usuario indicado en el comando.
     * <p>
     * Si el usuario ya es voluntario, se devuelve la instancia existente sin modificar nada (idempotencia).
     * Si no posee un perfil legal activo, se lanza una excepción indicando que el perfil está incompleto.
     *
     * @param command Objeto con los datos necesarios: ID del usuario, disponibilidad y especialidad.
     * @return El objeto {@link es.refugio.refugio.domain.model.voluntario.Voluntario} recién creado o existente.
     * @throws IllegalStateException Si el usuario no tiene un perfil legal registrado en el sistema.
     */
    public Voluntario create(CreateVoluntarioCommand command, boolean isAdmin, String adminToken) {
        // 1. Verificar si ya es voluntario (Idempotencia)
        var existing = voluntarioRepository.findByUsuarioId(command.usuarioId());
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2. Verificar que tiene PerfilLegal (Identidad)
        perfilLegalRepository.findByUsuarioId(command.usuarioId().getValue())
                .orElseThrow(() -> new IllegalStateException("error.voluntario.perfil_incompleto"));

        Voluntario voluntario = Voluntario.builder()
                .usuarioId(command.usuarioId())
                .disponibilidad(command.disponibilidad())
                .especialidad(command.especialidad())
                .estado(isAdmin ? EstadoVoluntario.APROBADO : EstadoVoluntario.PENDIENTE)
                .build();

        Voluntario saved = voluntarioRepository.save(voluntario);

        if (!isAdmin) {
            // Notificar a los Administradores (por ROL)
            notificacionService.enviarARol(
                    "ROLE_ADMIN",
                    "notificacion.voluntario.nueva_solicitud.titulo",
                    "notificacion.voluntario.nueva_solicitud.mensaje",
                    "SISTEMA",
                    "/web/voluntarios/pendientes");

            // Notificar al propio Voluntario (Personalizado por ID)
            if (command.usuarioId() != null) {
                notificacionService.enviar(
                        command.usuarioId().getValue(),
                        "notificacion.voluntario.solicitud_recibida.titulo",
                        "notificacion.voluntario.solicitud_recibida.mensaje",
                        "SISTEMA",
                        "/web/home");
            }
        } else {
            // Es ADMIN, auto-aprobar y actualizar rol
            if (adminToken != null && !adminToken.isEmpty()) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + adminToken);
                    HttpEntity<Void> entity = new HttpEntity<>(headers);

                    ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                            AUTH_SERVICE_URL + command.usuarioId().getValue(),
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<Map<String, Object>>() {}
                    );

                    if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
                        String currentRol = (String) userResponse.getBody().get("rol");
                        String newRol = "ROLE_VOLUNTARIO";

                        if ("ROLE_ADMIN".equals(currentRol)) {
                            newRol = "ROLE_ADMIN"; // Mantener admin si ya lo es
                        } else if ("ROLE_ADOPTANTE".equals(currentRol)) {
                            newRol = "ROLE_VOLUNTARIO_ADOPTANTE";
                        }

                        Map<String, String> roleBody = Map.of("rol", newRol);
                        HttpEntity<Map<String, String>> roleRequest = new HttpEntity<>(roleBody, headers);

                        restTemplate.exchange(
                                AUTH_SERVICE_URL + command.usuarioId().getValue() + "/rol",
                                HttpMethod.PUT,
                                roleRequest,
                                Map.class
                        );
                    }
                } catch (Exception e) {
                    // Log the error but don't fail the transaction
                    System.err.println("Error actualizando rol del voluntario desde ADMIN: " + e.getMessage());
                }
            }

            // Notificar al usuario que ha sido dado de alta como voluntario
            if (command.usuarioId() != null) {
                notificacionService.enviar(
                        command.usuarioId().getValue(),
                        "¡Bienvenido al Equipo!",
                        "Has sido registrado como voluntario en el refugio. Ya puedes acceder a la gestión de tareas.",
                        "SISTEMA",
                        "/web/tareas"
                );

                notificacionService.enviar(
                        command.usuarioId().getValue(),
                        "Configura tu Disponibilidad",
                        "Es necesario que indiques qué días estarás disponible en tu calendario para poder asignarte tareas operativas.",
                        "SISTEMA",
                        "/web/personas/" + command.usuarioId().getValue()
                );
            }
        }

        return saved;
    }
}