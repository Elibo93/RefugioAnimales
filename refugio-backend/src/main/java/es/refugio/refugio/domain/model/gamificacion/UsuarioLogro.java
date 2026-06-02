package es.refugio.refugio.domain.model.gamificacion;

import java.time.LocalDateTime;

public class UsuarioLogro {
    private Long usuarioId;
    private Long logroId;
    private LocalDateTime fechaDesbloqueo;

    public UsuarioLogro() {}

    public UsuarioLogro(Long usuarioId, Long logroId, LocalDateTime fechaDesbloqueo) {
        this.usuarioId = usuarioId;
        this.logroId = logroId;
        this.fechaDesbloqueo = fechaDesbloqueo;
    }

    // Getters y Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getLogroId() { return logroId; }
    public void setLogroId(Long logroId) { this.logroId = logroId; }
    public LocalDateTime getFechaDesbloqueo() { return fechaDesbloqueo; }
    public void setFechaDesbloqueo(LocalDateTime fechaDesbloqueo) { this.fechaDesbloqueo = fechaDesbloqueo; }
}
