package es.refugio.animales.refugio.application.usecase.voluntario;

import java.util.List;

import es.refugio.animales.refugio.domain.error.VoluntarioNotFoundException;
import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import es.refugio.animales.refugio.domain.repository.VoluntarioRepository;
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
}

















