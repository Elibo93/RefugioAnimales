package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoVoluntario;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;
    private final PerfilLegalRepository perfilLegalRepository;
    private final NotificacionService notificacionService;

    public Voluntario create(CreateVoluntarioCommand command) {
        // 1. Verificar si ya es voluntario (Idempotencia)
        var existing = voluntarioRepository.findByUsuarioId(command.usuarioId());
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2. Verificar que tiene PerfilLegal (Identidad)
        perfilLegalRepository.findByUsuarioId(command.usuarioId().getValue())
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario debe tener un perfil legal completo antes de ser voluntario"));

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
                "Nueva Solicitud de Voluntariado",
                "Un usuario ha solicitado unirse como voluntario y espera aprobación.",
                "SISTEMA",
                "/web/voluntarios/pendientes");

        // Notificar al propio Voluntario (Personalizado por ID)
        if (command.usuarioId() != null) {
            notificacionService.enviar(
                    command.usuarioId().getValue(),
                    "Solicitud Recibida",
                    "Tu solicitud para ser voluntario ha sido recibida correctamente. Te avisaremos cuando un administrador la revise.",
                    "SISTEMA",
                    "/web/home");
        }

        return saved;
    }
}