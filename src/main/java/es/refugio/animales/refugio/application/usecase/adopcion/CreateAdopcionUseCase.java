package es.refugio.animales.refugio.application.usecase.adopcion;

import java.time.LocalDateTime;

import es.refugio.animales.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import es.refugio.animales.refugio.domain.repository.AdopcionRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateAdopcionUseCase {
    private final AdopcionRepository adopcionRepository;

    public Adopcion create(CreateAdopcionCommand comando) {
        Adopcion adopcion = Adopcion.builder()
                .personaId(comando.personaId())
                .animalId(comando.animalId())
                .createdAt(LocalDateTime.now())
                .build();

        return adopcionRepository.save(adopcion);
    }
}
