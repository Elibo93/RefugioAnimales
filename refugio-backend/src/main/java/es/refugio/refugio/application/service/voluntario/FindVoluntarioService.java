package es.refugio.refugio.application.service.voluntario;

import java.util.List;
import es.refugio.refugio.application.usecase.voluntario.FindVoluntarioUseCase;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindVoluntarioService {

    private final FindVoluntarioUseCase useCase;

    public List<Voluntario> findAll() {
        return useCase.findAll();
    }

    public Voluntario findById(VoluntarioId id) {
        return useCase.findById(id);
    }

    public Voluntario findByUsuarioId(UsuarioId usuarioId) {
        return useCase.findByUsuarioId(usuarioId);
    }
}
