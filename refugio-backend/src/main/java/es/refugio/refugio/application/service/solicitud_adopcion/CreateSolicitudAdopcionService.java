package es.refugio.refugio.application.service.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.usecase.solicitud_adopcion.CreateSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateSolicitudAdopcionService {

    private final CreateSolicitudAdopcionUseCase useCase;

    public SolicitudAdopcion create(CreateSolicitudAdopcionCommand command) {
        return useCase.create(command);
    }
}
