package es.refugio.refugio.application.service.usuario;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.usecase.usuario.DeleteUsuarioUseCase;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DeleteUsuarioService {

    private final DeleteUsuarioUseCase deletePersonaUseCase;

    public void delete(UsuarioId id) {
        deletePersonaUseCase.delete(id);
    }

}
