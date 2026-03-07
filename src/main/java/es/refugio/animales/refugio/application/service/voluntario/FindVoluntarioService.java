package es.refugio.animales.refugio.application.service.voluntario;

import java.util.List;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.usecase.voluntario.FindVoluntarioUseCase;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindVoluntarioService {

    private final FindVoluntarioUseCase findVoluntarioUseCase;

    public List<Voluntario> findAll() {
        return findVoluntarioUseCase.findAll();
    }
}

















