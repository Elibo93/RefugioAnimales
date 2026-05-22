package es.refugio.refugio.application.service.voluntario;

import java.util.List;
import es.refugio.refugio.application.usecase.voluntario.FindVoluntarioUseCase;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class FindVoluntarioService {

    private final FindVoluntarioUseCase useCase;

    public List<Voluntario> findAll() {
        return useCase.findAll();
    }

    public Page<Voluntario> findAll(Pageable pageable) {
        return useCase.findAll(pageable);
    }

    public Page<Voluntario> findFiltered(String q, Integer excludeTareaId, String excludeDate, Pageable pageable) {
        return useCase.findFiltered(q, excludeTareaId, excludeDate, pageable);
    }

    public Voluntario findById(VoluntarioId id) {
        return useCase.findById(id);
    }

    public Voluntario findByUsuarioId(UsuarioId usuarioId) {
        return useCase.findByUsuarioId(usuarioId);
    }
}
