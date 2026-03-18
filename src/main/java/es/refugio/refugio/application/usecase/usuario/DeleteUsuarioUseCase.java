package es.refugio.refugio.application.usecase.usuario;

import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteUsuarioUseCase {
    public final UsuarioRepository personaRepository;

    public void delete(UsuarioId id) {
        personaRepository.deleteById(id);
    }

}
