package es.refugio.refugio.application.usecase.adopcion;

import java.util.List;

import es.refugio.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;

    public List<Adopcion> findAll() {
        List<Adopcion> adopciones = adopcionRepository.getAll();

        if (adopciones.isEmpty())
            throw new AdopcionNotFoundException();

        return adopciones;
    }

    public Adopcion findById(AdopcionId id) {
        return adopcionRepository.getById(id).orElseThrow(() -> new AdopcionNotFoundException(id.getValue()));
    }

    public List<Adopcion> findByCriteria(Integer PersonaId, Integer AnimalId) {
        if (PersonaId != null && AnimalId != null) {
            // Intersección
            List<Adopcion> porPersona = adopcionRepository.getByPersonaId(PersonaId);
            return porPersona.stream()
                    .filter(i -> i.getAnimalId().getValue().equals(AnimalId))
                    .collect(java.util.stream.Collectors.toList());
        }

        if (PersonaId != null) {
            return adopcionRepository.getByPersonaId(PersonaId);
        }

        if (AnimalId != null) {
            return adopcionRepository.getByAnimalId(AnimalId);
        }

        return findAll();
    }
}
