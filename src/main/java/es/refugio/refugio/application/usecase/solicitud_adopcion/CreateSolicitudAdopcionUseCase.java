package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;

    public SolicitudAdopcion create(CreateSolicitudAdopcionCommand command) {
        EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(command.estado().toUpperCase());
        
        SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                .animalId(new AnimalId(command.animalId()))
                .adoptanteId(new AdoptanteId(command.adoptanteId()))
                .fecha(command.fecha())
                .estado(estadoEnum)
                .comentario(command.comentario())
                .build();
                
        return solicitudAdopcionRepository.save(solicitud);
    }
}
