package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.domain.model.donacion.ObjetivoDonacion;
import es.refugio.refugio.domain.repository.ObjetivoDonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Create Objetivo Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class CreateObjetivoDonacionUseCase {

    private final ObjetivoDonacionRepository repository;

    public ObjetivoDonacion create(ObjetivoDonacion objetivo) {
        if (objetivo.getMontoRecaudado() == null) {
            objetivo.setMontoRecaudado(0.0);
        }
        return repository.save(objetivo);
    }
}
