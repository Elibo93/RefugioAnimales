package es.refugio.refugio.domain.event.donacion;

import java.math.BigDecimal;

public class DonacionCompletedEvent {
    private final Long usuarioId;
    private final BigDecimal monto;

    public DonacionCompletedEvent(Long usuarioId, BigDecimal monto) {
        this.usuarioId = usuarioId;
        this.monto = monto;
    }

    public Long getUsuarioId() { return usuarioId; }
    public BigDecimal getMonto() { return monto; }
}
