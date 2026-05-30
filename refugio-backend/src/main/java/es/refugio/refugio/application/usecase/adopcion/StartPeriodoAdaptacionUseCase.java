package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class StartPeriodoAdaptacionUseCase {

    private final AdopcionRepository adopcionRepository;
    private final AnimalRepository animalRepository;

    @Transactional
    public Adopcion startPeriodoAdaptacion(Integer adopcionId) {
        return adopcionRepository.getById(new AdopcionId(adopcionId))
                .map(adopcion -> {
                    if (adopcion.getEstado() != EstadoAdopcion.PENDIENTE_FIRMA) {
                        throw new IllegalStateException("error.adopcion.debe.estar.pendiente");
                    }
                    
                    adopcion.setEstado(EstadoAdopcion.EN_PERIODO_ADAPTACION);
                    
                    animalRepository.getById(adopcion.getAnimalId()).ifPresent(animal -> {
                        animal.setEstado(EstadoAnimal.ADOPTADO);
                        animalRepository.save(animal);
                    });
                    
                    return adopcionRepository.save(adopcion);
                })
                .orElseThrow(() -> new AdopcionNotFoundException(adopcionId));
    }
}
