package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditAdopcionUseCase {
    private final AdopcionRepository adopcionRepository;

   public Adopcion update(EditAdopcionCommand command) {
       return adopcionRepository.getById(command.id())
               .map(p -> { // Actualizamos los atributos del objeto
                   p.setAnimalId(command.animalId());
                   p.setUsuarioId(command.usuarioId());
                   return adopcionRepository.save(p);
               })
               .orElseThrow(() -> new AdopcionNotFoundException(command.id().getValue())); // Lo cambiamos

   }

}

















