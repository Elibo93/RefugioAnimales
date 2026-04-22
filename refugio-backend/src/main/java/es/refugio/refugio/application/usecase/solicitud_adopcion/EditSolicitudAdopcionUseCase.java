package es.refugio.refugio.application.usecase.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.enums.EstadoSolicitud;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;

    public SolicitudAdopcion update(EditSolicitudAdopcionCommand command) {
        return solicitudAdopcionRepository.getById(command.id())
                .map(solicitud -> {
                    EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(command.estado().toUpperCase());
                    
                    solicitud.setFecha(command.fecha());
                    solicitud.setEstado(estadoEnum);
                    solicitud.setComentario(command.comentario());
                    
                    return solicitudAdopcionRepository.save(solicitud);
                })
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(command.id().getValue()));
    }
}
