package es.refugio.refugio.application.usecase.donacion;

import java.util.List;
import es.refugio.refugio.domain.error.DonacionNotFoundException;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Find Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public List<Donacion> findAll() {
        return donacionRepository.getAll();
    }

    public Page<Donacion> findAll(Pageable pageable) {
        return donacionRepository.findAll(pageable);
    }

    public Donacion findById(DonacionId id) {
        return donacionRepository.getById(id)
                .orElseThrow(() -> new DonacionNotFoundException(id.getValue()));
    }

    public List<Donacion> findByUsuarioId(UsuarioId usuarioId) {
        return donacionRepository.getByUsuarioId(usuarioId);
    }
}
