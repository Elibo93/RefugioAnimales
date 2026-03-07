package es.refugio.animales.refugio.application.usecase.adopcion;

import es.refugio.animales.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.animales.refugio.domain.repository.AdopcionRepository;
import es.refugio.animales.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditAdopcionUseCase {
    private final AdopcionRepository adopcionRepository;

   public Adopcion update(EditAdopcionCommand command) {
       return adopcionRepository.getById(command.id())
               .map(p -> { // Actualizamos los atributos del objeto
                   p.setAnimalId(command.animalId());
                   p.setPersonaId(command.personaId());
                   return adopcionRepository.save(p);
               })
               .orElseThrow(() -> new AdopcionNotFoundException(command.id().getValue())); // Lo cambiamos

   }

}

















