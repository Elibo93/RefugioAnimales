package es.refugio.refugio.application.usecase.donacion;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.donacion.ObjetivoDonacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.repository.ObjetivoDonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindObjetivoDonacionUseCase {

    private final ObjetivoDonacionRepository repository;

    public List<ObjetivoDonacion> findAll() {
        return repository.getAll();
    }

    public Optional<ObjetivoDonacion> findById(ObjetivoDonacionId id) {
        return repository.getById(id);
    }
}
