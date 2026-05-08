package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.domain.error.SolicitudAdopcionEstadoInvalidoException;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
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
 * Caso de uso orquestador: Aprueba una solicitud de adopción.
 *
 * Pasos que realiza en una única transacción:
 *  1. Busca y valida que la solicitud está en estado PENDIENTE
 *  2. Actualiza la solicitud → APROBADA
 *  3. Actualiza el animal → RESERVADO
 *  4. Actualiza el adoptante → APROBADO
 *  5. Crea la Adopcion con estado PENDIENTE_FIRMA
 */
@RequiredArgsConstructor
public class AprobarSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AnimalRepository animalRepository;
    private final AdoptanteRepository adoptanteRepository;
    private final AdopcionRepository adopcionRepository;
    private final es.refugio.refugio.application.service.NotificacionService notificacionService;

    @Transactional
    public Adopcion aprobar(SolicitudAdopcionId solicitudId) {

        // 1 — Buscar y validar la solicitud
        SolicitudAdopcion solicitud = solicitudAdopcionRepository.getById(solicitudId)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(solicitudId.getValue()));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new SolicitudAdopcionEstadoInvalidoException(solicitud.getEstado().name());
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
                            "/web/solicitudes/mis-adoptados"
                        );
                    }
                });
        // No lanzamos error si el adoptante no existe; la solicitud puede precederle

        // 5 — Crear la Adopcion con estado PENDIENTE_FIRMA
        Adopcion nuevaAdopcion = Adopcion.builder()
                .adoptanteId(solicitud.getAdoptanteId())
                .animalId(solicitud.getAnimalId())
                .fechaAdopcion(LocalDateTime.now())
                .estado(EstadoAdopcion.PENDIENTE_FIRMA)
                .build();

        return adopcionRepository.save(nuevaAdopcion);
    }
}
