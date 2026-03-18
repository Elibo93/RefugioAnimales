package es.refugio.refugio.application.service.adopcion;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.application.usecase.adopcion.EditAdopcionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EditAdopcionService {
    private final EditAdopcionUseCase editAdopcionUseCase;

    public Adopcion update(EditAdopcionCommand comando) {
        Adopcion adopcion = editAdopcionUseCase.update(comando);
        return adopcion;
    }

}

















