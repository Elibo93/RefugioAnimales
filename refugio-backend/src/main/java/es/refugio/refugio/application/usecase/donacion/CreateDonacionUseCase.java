package es.refugio.refugio.application.usecase.donacion;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import es.refugio.refugio.domain.repository.ObjetivoDonacionRepository;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateDonacionUseCase {

    private final DonacionRepository donacionRepository;
    private final ObjetivoDonacionRepository objetivoRepository;

    public Donacion create(CreateDonacionCommand command) {
        Integer targetUserId = command.usuarioId();

        // Si no hay usuarioId (donación anónima), buscar el usuario 'anonimo@refugio.es'
        if (targetUserId == null) {
            targetUserId = 2; // Mock anonymous user ID
        }

        Donacion donacion = Donacion.builder()
                .usuarioId(new UsuarioId(targetUserId))
                .objetivoId(command.objetivoId() != null ? new ObjetivoDonacionId(command.objetivoId()) : null)
                .tipo(command.tipo())
                .frecuencia(command.frecuencia())
                .cantidad(command.cantidad())
                .fecha(command.fecha())
                .descripcion(command.descripcion())
                .build();

        Donacion savedDonacion = donacionRepository.save(donacion);

        // Actualizar monto recaudado si hay un objetivo vinculado
        if (command.objetivoId() != null) {
            objetivoRepository.getById(new ObjetivoDonacionId(command.objetivoId()))
                    .ifPresent(objetivo -> {
                        double nuevoMonto = objetivo.getMontoRecaudado() + command.cantidad();
                        objetivo.setMontoRecaudado(nuevoMonto);
                        objetivoRepository.save(objetivo);
                    });
        }

        return savedDonacion;
    }
}
