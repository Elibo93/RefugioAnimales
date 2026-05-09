package es.refugio.refugio.application.service;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repository;

    @Transactional
    public void enviar(Integer usuarioId, String titulo, String mensaje, String tipo, String enlace) {
        NotificacionEntity notif = NotificacionEntity.builder()
                .usuarioId(usuarioId)
                .titulo(titulo)
                .mensaje(mensaje)
                .fecha(LocalDateTime.now())
                .leida(false)
                .tipo(tipo)
                .enlace(enlace)
                .build();
        repository.save(notif);
    }
}
