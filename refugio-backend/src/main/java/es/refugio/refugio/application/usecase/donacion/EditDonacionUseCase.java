package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.application.command.donacion.EditDonacionCommand;
import es.refugio.refugio.domain.error.DonacionNotFoundException;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Edit Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public Donacion update(EditDonacionCommand command) {
        return donacionRepository.getById(command.id())
                .map(donacion -> {
                    donacion.setUsuarioId(new UsuarioId(command.usuarioId()));
                    donacion.setTipo(command.tipo());
                    donacion.setFrecuencia(command.frecuencia());
                    donacion.setCantidad(command.cantidad());
                    donacion.setFecha(command.fecha());
                    donacion.setProximaFechaPago(command.proximaFechaPago());
                    donacion.setDescripcion(command.descripcion());

                    if (command.objetivoId() != null) {
                        donacion.setObjetivoId(new ObjetivoDonacionId(command.objetivoId()));
                    } else {
                        donacion.setObjetivoId(null);
                    }

                    return donacionRepository.save(donacion);
                })
                .orElseThrow(() -> new DonacionNotFoundException(command.id().getValue()));
    }
}
