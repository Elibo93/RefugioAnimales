package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateDonacionUseCase {

    private final DonacionRepository donacionRepository;

    public Donacion create(CreateDonacionCommand command) {
        TipoDonacion tipoEnum = TipoDonacion.valueOf(command.tipo().toUpperCase());
        FrecuenciaDonacion frecuenciaEnum = FrecuenciaDonacion.valueOf(command.frecuencia().toUpperCase());
        
        Donacion donacion = Donacion.builder()
                .usuarioId(new UsuarioId(command.usuarioId()))
                .tipo(tipoEnum)
                .frecuencia(frecuenciaEnum)
                .cantidad(command.cantidad())
                .fecha(command.fecha())
                .descripcion(command.descripcion())
                .build();
                
        return donacionRepository.save(donacion);
    }
}

