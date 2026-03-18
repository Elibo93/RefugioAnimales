package es.refugio.refugio.application.usecase.donacion;

import java.util.List;
import es.refugio.refugio.domain.error.DonacionNotFoundException;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public List<Donacion> findAll() {
        List<Donacion> donaciones = donacionRepository.getAll();
        if (donaciones.isEmpty()) {
            throw new DonacionNotFoundException();
        }
        return donaciones;
    }

    public Donacion findById(DonacionId id) {
        return donacionRepository.getById(id)
                .orElseThrow(() -> new DonacionNotFoundException(id.getValue()));
    }

    public List<Donacion> findByUsuarioId(UsuarioId usuarioId) {
        return donacionRepository.getByUsuarioId(usuarioId);
    }
}
