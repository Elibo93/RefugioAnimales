package es.refugio.refugio.application.usecase.adopcion;

import java.time.LocalDateTime;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateAdopcionUseCase {
    private final AdopcionRepository adopcionRepository;

    public Adopcion create(CreateAdopcionCommand comando) {
        Adopcion adopcion = Adopcion.builder()
                .usuarioId(comando.usuarioId())
                .animalId(comando.animalId())
                .createdAt(LocalDateTime.now())
                .build();

        return adopcionRepository.save(adopcion);
    }
}
