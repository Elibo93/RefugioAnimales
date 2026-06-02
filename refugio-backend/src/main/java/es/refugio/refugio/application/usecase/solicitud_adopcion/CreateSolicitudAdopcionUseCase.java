package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.error.AnimalNotFoundException;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Create Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AnimalRepository animalRepository;
    private final NotificacionService notificacionService;

    public SolicitudAdopcion create(CreateSolicitudAdopcionCommand command) {
        AnimalId animalId = new AnimalId(command.animalId());
        AdoptanteId adoptanteId = new AdoptanteId(command.adoptanteId());

        // 1. Validar que el animal existe y no está adoptado
        var animal = animalRepository.getById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId.getValue()));

        if (animal.getEstado() == EstadoAnimal.ADOPTADO) {
            throw new IllegalStateException("error.solicitud.ya_adoptado");
        }

        // Evitar duplicidad de solicitudes pendientes para el mismo animal
        boolean yaTieneSolicitud = solicitudAdopcionRepository.getByAdoptanteId(adoptanteId).stream()
                .anyMatch(s -> s.getAnimalId().equals(animalId) && s.getEstado() == EstadoSolicitud.PENDIENTE);

        if (yaTieneSolicitud) {
            throw new IllegalStateException("error.solicitud.ya_pendiente");
        }

        SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                .animalId(animalId)
                .adoptanteId(adoptanteId)
                .fecha(command.fecha() != null ? command.fecha() : LocalDateTime.now())
                .estado(EstadoSolicitud.PENDIENTE)
                .comentario(command.comentario())
                .comentarioAdmin(command.comentarioAdmin())
                .build();

        SolicitudAdopcion savedSolicitud = solicitudAdopcionRepository.save(solicitud);

        // Notificar a los Administradores (por ROL)
        notificacionService.enviarARol(
                "ROLE_ADMIN",
                "Nueva Solicitud de Adopción",
                "Se ha recibido una nueva solicitud para adoptar a " + animal.getNombre(),
                "ADOPCION",
                "/web/solicitudes/" + savedSolicitud.getId().getValue() + "/detalle");

        return savedSolicitud;
    }
}