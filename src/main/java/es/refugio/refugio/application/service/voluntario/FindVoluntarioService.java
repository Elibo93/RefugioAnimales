package es.refugio.refugio.application.service.voluntario;

import java.util.List;

import org.springframework.stereotype.Service;

import es.refugio.refugio.application.usecase.voluntario.FindVoluntarioUseCase;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindVoluntarioService {

    private final FindVoluntarioUseCase findVoluntarioUseCase;

    public List<Voluntario> findAll() {
        return findVoluntarioUseCase.findAll();
    }

    public Voluntario findById(es.refugio.refugio.domain.model.voluntario.VoluntarioId id) {
        return findVoluntarioUseCase.findById(id);
    }
}

















