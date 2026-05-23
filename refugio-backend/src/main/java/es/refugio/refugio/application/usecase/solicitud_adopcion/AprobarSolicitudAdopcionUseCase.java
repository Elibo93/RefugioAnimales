package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.domain.error.SolicitudAdopcionEstadoInvalidoException;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.error.AnimalYaAdoptadoException;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.model.adoptante.enums.EstadoValidacion;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de uso que aprueba una solicitud de adopción pendiente.
 *
 * <p>En una única transacción, este caso de uso:
 * <ol>
 *   <li>Busca y valida que la solicitud está en estado {@code PENDIENTE} o {@code EN_REVISION}.</li>
 *   <li>Valida que el animal no tenga ya otra adopción activa.</li>
 *   <li>Cambia la solicitud a {@code APROBADA}.</li>
 *   <li>Cambia el estado del animal a {@code RESERVADO}.</li>
 *   <li>Actualiza el adoptante a {@code APROBADO}.</li>
 *   <li>Crea la entidad {@link es.refugio.refugio.domain.model.adopcion.Adopcion} en estado {@code PENDIENTE_FIRMA}.</li>
 *   <li>Envía notificaciones al adoptante y a los administradores.</li>
 * </ol>
 *
 * @author Elisabeth
 * @author Diego
 */
@RequiredArgsConstructor
public class AprobarSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AnimalRepository animalRepository;
    private final AdoptanteRepository adoptanteRepository;
    private final AdopcionRepository adopcionRepository;
    private final NotificacionService notificacionService;

    /**
     * Aprueba la solicitud de adopción identificada por el ID proporcionado,
     * actualizando en cadena el estado de la solicitud, el animal y el adoptante,
     * y creando la entidad de adopción definitiva.
     *
     * @param solicitudId Identificador único de la solicitud a aprobar.
     * @return La entidad {@link es.refugio.refugio.domain.model.adopcion.Adopcion} recién creada
     *         con estado {@code PENDIENTE_FIRMA}.
     * @throws SolicitudAdopcionNotFoundException      Si la solicitud no existe en el sistema.
     * @throws SolicitudAdopcionEstadoInvalidoException Si la solicitud no está en estado {@code PENDIENTE} ni {@code EN_REVISION}.
     * @throws AnimalYaAdoptadoException               Si el animal ya tiene una adopción vigente.
     */
    @Transactional
    public Adopcion aprobar(SolicitudAdopcionId solicitudId) {
        // 1 — Buscar y validar la solicitud
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.getById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId.getValue()));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE
                && solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            throw new SolicitudAdopcionEstadoInvalidoException(solicitud.getEstado().name());
        }

        // VALIDACIÓN: ¿El animal ya tiene una adopción?
        if (adopcionRepository.existsByAnimalId(solicitud.getAnimalId())) {
            throw new AnimalYaAdoptadoException(solicitud.getAnimalId().getValue());
        }

        // 2 — Actualizar solicitud → APROBADA
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitudAdopcionRepository.save(solicitud);

        // 3 — Actualizar animal → RESERVADO
        String animalNombre = animalRepository.getById(solicitud.getAnimalId())
                .map(animal -> {
                    animal.setEstado(EstadoAnimal.RESERVADO);
                    animalRepository.save(animal);
                    return animal.getNombre();
                })
                .orElse("el animal");

        // 4 — Actualizar adoptante → APROBADO e informar
        adoptanteRepository.getById(solicitud.getAdoptanteId())
                .ifPresent(adoptante -> {
                    adoptante.setEstadoValidacion(EstadoValidacion.APROBADO);
                    adoptanteRepository.save(adoptante);

                    if (adoptante.getUsuarioId() != null) {
                        notificacionService.enviar(
                                adoptante.getUsuarioId(),
                                "Solicitud de Adopción Aprobada",
                                "¡Buenas noticias! Tu solicitud para adoptar a " + animalNombre + " ha sido aprobada.",
                                "ADOPCION",
                                "/web/solicitudes/" + solicitud.getId().getValue() + "/detalle");
                    }
                });

        // 5 — Crear la Adopcion con estado PENDIENTE_FIRMA
        Adopcion nuevaAdopcion = Adopcion.builder()
                .adoptanteId(solicitud.getAdoptanteId())
                .animalId(solicitud.getAnimalId())
                .solicitudAdopcionId(solicitud.getId().getValue())
                .fechaAdopcion(LocalDateTime.now())
                .estado(EstadoAdopcion.PENDIENTE_FIRMA)
                .build();

        return adopcionRepository.save(nuevaAdopcion);
    }
}
