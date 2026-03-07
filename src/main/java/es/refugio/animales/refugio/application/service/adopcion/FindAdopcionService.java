package es.refugio.animales.refugio.application.service.adopcion;

import java.util.List;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.usecase.adopcion.FindAdopcionUseCase;
import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindAdopcionService {

    private final FindAdopcionUseCase findAdopcionUseCase;

    public List<Adopcion> findAll() {
        return findAdopcionUseCase.findAll();
    }

    public Adopcion findById(AdopcionId id) {
        return findAdopcionUseCase.findById(id);
    }

    public List<Adopcion> findByCriteria(Integer PersonaId, Integer AnimalId) {
        return findAdopcionUseCase.findByCriteria(PersonaId, AnimalId);
    }

}

















