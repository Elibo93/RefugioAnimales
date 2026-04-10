package es.refugio.refugio.application.usecase.voluntario;

import java.util.List;
import es.refugio.refugio.domain.error.VoluntarioNotFoundException;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindVoluntarioUseCase {

    private final VoluntarioRepository voluntarioRepository;

    public List<Voluntario> findAll() {
        List<Voluntario> voluntarios = voluntarioRepository.getAll();
        if (voluntarios.isEmpty()) {
            throw new VoluntarioNotFoundException();
        }
        return voluntarios;
    }

    public Voluntario findById(VoluntarioId id) {
        return voluntarioRepository.getById(id)
                .orElseThrow(() -> new VoluntarioNotFoundException(id.getValue()));
    }

    public Voluntario findByUsuarioId(UsuarioId usuarioId) {
        return voluntarioRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new VoluntarioNotFoundException());
    }
}
