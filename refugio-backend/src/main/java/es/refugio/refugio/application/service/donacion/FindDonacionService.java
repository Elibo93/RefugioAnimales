package es.refugio.refugio.application.service.donacion;

import java.util.List;
import es.refugio.refugio.application.usecase.donacion.FindDonacionUseCase;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class FindDonacionService {

    private final FindDonacionUseCase useCase;

    public List<Donacion> findAll() {
        return useCase.findAll();
    }

    public Page<Donacion> findAll(Pageable pageable) {
        return useCase.findAll(pageable);
    }

    public Donacion findById(DonacionId id) {
        return useCase.findById(id);
    }

    public List<Donacion> findByUsuarioId(UsuarioId usuarioId) {
        return useCase.findByUsuarioId(usuarioId);
    }
}
