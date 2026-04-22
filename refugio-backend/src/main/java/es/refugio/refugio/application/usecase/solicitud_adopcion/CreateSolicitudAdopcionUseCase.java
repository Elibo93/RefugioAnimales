package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;
    private final AnimalRepository animalRepository;

    public SolicitudAdopcion create(CreateSolicitudAdopcionCommand command) {
        AnimalId animalId = new AnimalId(command.animalId());
        AdoptanteId adoptanteId = new AdoptanteId(command.adoptanteId());

        // 1. Validar que el animal existe y no está adoptado
        var animal = animalRepository.getById(animalId)
                .orElseThrow(() -> new es.refugio.refugio.domain.error.AnimalNotFoundException(animalId.getValue()));

        if (animal.getEstado() == EstadoAnimal.ADOPTADO) {
            throw new IllegalStateException("Este animal ya ha sido adoptado.");
        }

        // Evitar duplicidad de solicitudes pendientes para el mismo animal
        boolean yaTieneSolicitud = solicitudAdopcionRepository.getByAdoptanteId(adoptanteId).stream()
                .anyMatch(s -> s.getAnimalId().equals(animalId) && s.getEstado() == EstadoSolicitud.PENDIENTE);

        if (yaTieneSolicitud) {
            throw new IllegalStateException("Ya tienes una solicitud pendiente para este animal.");
        }

        SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                .animalId(animalId)
                .adoptanteId(adoptanteId)
                .fecha(command.fecha() != null ? command.fecha() : java.time.LocalDateTime.now())
                .estado(EstadoSolicitud.PENDIENTE)
                .comentario(command.comentario())
                .build();

        SolicitudAdopcion savedSolicitud = solicitudAdopcionRepository.save(solicitud);

        // Reservar animal tras recibir solicitud
        animal.setEstado(EstadoAnimal.RESERVADO);
        animalRepository.save(animal);

        return savedSolicitud;
    }
}
