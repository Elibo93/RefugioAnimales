package es.refugio.refugio.application.usecase.voluntario;

import java.util.List;

import es.refugio.refugio.domain.error.VoluntarioNotFoundException;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindVoluntarioUseCase {

    // Atributos
    private final VoluntarioRepository voluntarioRepository;

    public List<Voluntario> findAll() {
        List<Voluntario> voluntarios = voluntarioRepository.getAll();

        if (voluntarios.isEmpty())
            throw new VoluntarioNotFoundException();

        return voluntarios;
    }

    public Voluntario findById(es.refugio.refugio.domain.model.voluntario.VoluntarioId id) {
        return voluntarioRepository.getById(id)
                .orElseThrow(() -> new VoluntarioNotFoundException());
    }
}

















