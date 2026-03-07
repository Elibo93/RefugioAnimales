package es.refugio.animales.refugio.application.service.adopcion;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.usecase.adopcion.DeleteAdopcionUseCase;
import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DeleteAdopcionService {

    private final DeleteAdopcionUseCase deleteAdopcionUseCase;

    public void delete(AdopcionId id) {
        deleteAdopcionUseCase.delete(id);
    }
    

}

















