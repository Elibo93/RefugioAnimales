package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

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
    public Voluntario create(CreateVoluntarioCommand command) {
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
                .estado(EstadoVoluntario.PENDIENTE)
                .build();

        Voluntario saved = voluntarioRepository.save(voluntario);

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

        return saved;
    }
}