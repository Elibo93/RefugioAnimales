package es.refugio.animales.refugio.application.service.adopcion;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.animales.refugio.application.usecase.adopcion.CreateAdopcionUseCase;
import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateAdopcionService {

    private final CreateAdopcionUseCase createAdopcionUseCase;

    public Adopcion createAdopcion(CreateAdopcionCommand comando) {
        Adopcion adopcion = createAdopcionUseCase.create(comando);
        return adopcion;
        
    }

}

















