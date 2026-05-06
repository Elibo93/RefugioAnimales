package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.refugio.domain.repository.DonacionRepository;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public Donacion create(CreateDonacionCommand command) {
        TipoDonacion tipoEnum = TipoDonacion.valueOf(command.tipo().toUpperCase());
        FrecuenciaDonacion frecuenciaEnum = FrecuenciaDonacion.valueOf(command.frecuencia().toUpperCase());

        Integer targetUserId = command.usuarioId();

        // Si no hay usuarioId (donación anónima), buscar el usuario
        // 'anonimo@refugio.es'
        if (targetUserId == null) {
            // TODO: Fetch anonymous user from refugio-auth Feign
            targetUserId = 2; // Mock anonymous user ID
        }

        LocalDateTime fecha = command.fecha() != null ? command.fecha() : LocalDateTime.now();
        LocalDateTime proximaFechaPago = null;

        if (frecuenciaEnum == FrecuenciaDonacion.MENSUAL) {
            proximaFechaPago = fecha.plusMonths(1);
        }

        Donacion donacion = Donacion.builder()
                .usuarioId(new UsuarioId(targetUserId))
                .tipo(tipoEnum)
                .frecuencia(frecuenciaEnum)
                .cantidad(command.cantidad())
                .fecha(fecha)
                .proximaFechaPago(proximaFechaPago)
                .descripcion(command.descripcion())
                .build();

        return donacionRepository.save(donacion);
    }
}
