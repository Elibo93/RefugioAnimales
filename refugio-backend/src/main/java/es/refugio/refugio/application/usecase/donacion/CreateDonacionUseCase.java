package es.refugio.refugio.application.usecase.donacion;
 
import es.refugio.refugio.application.service.NotificacionService;

import es.refugio.refugio.application.command.donacion.CreateDonacionCommand;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import es.refugio.refugio.domain.repository.ObjetivoDonacionRepository;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateDonacionUseCase {

    private final DonacionRepository donacionRepository;
    private final ObjetivoDonacionRepository objetivoRepository;
    private final NotificacionService notificacionService;

    public Donacion create(CreateDonacionCommand command) {
        Integer targetUserId = command.getUsuarioId();

        // Si no hay usuarioId (donación anónima), buscar el usuario 'anonimo@refugio.es'
        // El ID 2 corresponde al usuario anónimo en el seed data
        if (targetUserId == null) {
            targetUserId = 2; 
        }

        Donacion donacion = Donacion.builder()
                .usuarioId(new UsuarioId(targetUserId))
                .objetivoId(command.getObjetivoId() != null ? new ObjetivoDonacionId(command.getObjetivoId()) : null)
                .tipo(command.getTipo())
                .frecuencia(command.getFrecuencia())
                .cantidad(command.getCantidad())
                .fecha(command.getFecha() != null ? command.getFecha() : LocalDateTime.now())
                .proximaFechaPago(command.getProximaFechaPago())
                .descripcion(command.getDescripcion())
                .build();

        Donacion savedDonacion = donacionRepository.save(donacion);

        // Actualizar monto recaudado si hay un objetivo vinculado
        if (command.getObjetivoId() != null) {
            objetivoRepository.getById(new ObjetivoDonacionId(command.getObjetivoId()))
                    .ifPresent(objetivo -> {
                        double anterior = objetivo.getMontoRecaudado() != null ? objetivo.getMontoRecaudado() : 0.0;
                        double nuevoMonto = anterior + command.getCantidad();
                        objetivo.setMontoRecaudado(nuevoMonto);
                        objetivoRepository.save(objetivo);

                        // Notificación especial si se alcanza el objetivo
                        if (objetivo.getMontoObjetivo() != null && anterior < objetivo.getMontoObjetivo() && nuevoMonto >= objetivo.getMontoObjetivo()) {
                            try {
                                String titulo = "🎯 ¡Objetivo Alcanzado!";
                                String mensaje = String.format("¡Felicidades! Se ha completado el objetivo: '%s'. Se han recaudado %.2f€.", 
                                        objetivo.getTitulo(), nuevoMonto);
                                notificacionService.enviar(1, titulo, mensaje, "OBJETIVO_CUMPLIDO", "/web/donaciones");
                            } catch (Exception e) {
                                // No bloqueamos la donación si falla la notificación
                            }
                        }
                    });
        }

        return savedDonacion;
    }
}
