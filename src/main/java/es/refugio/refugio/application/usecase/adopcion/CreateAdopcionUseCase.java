package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;

    public Adopcion create(CreateAdopcionCommand command) {
        EstadoAdopcion estadoEnum = EstadoAdopcion.valueOf(command.estado().toUpperCase());
        
        Adopcion adopcion = Adopcion.builder()
                .animalId(new AnimalId(command.animalId()))
                .adoptanteId(new AdoptanteId(command.adoptanteId()))
                .fechaAdopcion(command.fechaAdopcion())
                .estado(estadoEnum)
                .contrato(command.contrato())
                .build();
                
        return adopcionRepository.save(adopcion);
    }
}
