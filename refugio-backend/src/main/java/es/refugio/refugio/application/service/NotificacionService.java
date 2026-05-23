package es.refugio.refugio.application.service;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Notificacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class NotificacionService {

    private final NotificacionRepository repository;

    /**
     * Crea y persiste una notificación dirigida a un usuario específico.
     *
     * @param usuarioId ID del usuario destinatario de la notificación.
     * @param titulo    Título breve de la notificación.
     * @param mensaje   Cuerpo del mensaje de la notificación.
     * @param tipo      Categoría de la notificación (por ejemplo: {@code "MATCH"}, {@code "ADOPCION"}, {@code "SISTEMA"}).
     * @param enlace    URL relativa a la que redireccionar al usuario al pulsar la notificación.
     */
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

    /**
     * Crea y persiste una notificación dirigida a todos los usuarios que posean un rol concreto.
     * Se utiliza, por ejemplo, para avisar a los administradores de nuevas solicitudes.
     *
     * @param rol     Rol de Spring Security al que va dirigida la notificación (por ejemplo: {@code "ROLE_ADMIN"}).
     * @param titulo  Título breve de la notificación (puede ser una clave i18n).
     * @param mensaje Cuerpo del mensaje (puede ser una clave i18n).
     * @param tipo    Categoría de la notificación.
     * @param enlace  URL relativa al recurso relacionado.
     */
    @Transactional
    public void enviarARol(String rol, String titulo, String mensaje, String tipo, String enlace) {
        NotificacionEntity notif = NotificacionEntity.builder()
                .rol(rol)
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
