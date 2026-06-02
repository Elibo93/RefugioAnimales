package es.refugio.refugio.application.service.adoptante;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.usecase.adoptante.DeleteAdoptanteUseCase;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Delete Adoptante.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteAdoptanteService {

    private final DeleteAdoptanteUseCase deleteAdoptanteUseCase;

    public void delete(AdoptanteId id) {
        // Delegamos la acción de eliminar al caso de uso correspondiente
        deleteAdoptanteUseCase.delete(id);
    }
}

