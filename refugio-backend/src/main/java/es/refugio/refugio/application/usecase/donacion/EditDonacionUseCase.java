package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.domain.error.DonacionNotFoundException;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public Donacion update(EditDonacionCommand command) {
        return donacionRepository.getById(command.id())
                .map(donacion -> {
                    TipoDonacion tipoEnum = TipoDonacion.valueOf(command.tipo().toUpperCase());
                    FrecuenciaDonacion frecuenciaEnum = FrecuenciaDonacion.valueOf(command.frecuencia().toUpperCase());

                    donacion.setUsuarioId(new UsuarioId(command.usuarioId()));
                    donacion.setTipo(tipoEnum);
                    donacion.setFrecuencia(frecuenciaEnum);
                    donacion.setCantidad(command.cantidad());
                    donacion.setFecha(command.fecha());
                    donacion.setDescripcion(command.descripcion());

                    return donacionRepository.save(donacion);
                })
                .orElseThrow(() -> new DonacionNotFoundException(command.id().getValue()));
    }
}

