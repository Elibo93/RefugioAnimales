package es.refugio.refugio.application.usecase.voluntario;

import es.refugio.refugio.application.command.voluntario.CreateVoluntarioCommand;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;
    private final es.refugio.refugio.domain.repository.PerfilLegalRepository perfilLegalRepository;
    private final es.refugio.refugio.application.service.NotificacionService notificacionService;

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
                .build();

        Voluntario saved = voluntarioRepository.save(voluntario);

        // Notificar a los Administradores (por ROL)
        notificacionService.enviarARol(
                "ROLE_ADMIN",
                "Nueva Petición de Voluntariado",
                "Un usuario ha solicitado unirse como voluntario.",
                "SISTEMA",
                "/web/voluntarios/" + saved.getId().getValue());

        // Notificar al propio Voluntario (Personalizado por ID)
        if (command.usuarioId() != null) {
            notificacionService.enviar(
                    command.usuarioId().getValue(),
                    "Bienvenido al Refugio",
                    "¡Felicidades! Has sido aceptado como voluntario. Ya puedes ver tus tareas asignadas.",
                    "SISTEMA",
                    "/web/tareas");
        }

        return saved;
    }
}