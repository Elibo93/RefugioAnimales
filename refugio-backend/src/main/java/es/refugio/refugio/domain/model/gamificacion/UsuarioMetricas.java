package es.refugio.refugio.domain.model.gamificacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UsuarioMetricas {
    private Long usuarioId;
    private int tareasCompletadas;
    private BigDecimal totalDonado;
    private LocalDateTime fechaPrimerAporte;
    private LocalDateTime ultimaActualizacion;

    public UsuarioMetricas() {
        this.tareasCompletadas = 0;
        this.totalDonado = BigDecimal.ZERO;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public UsuarioMetricas(Long usuarioId, int tareasCompletadas, BigDecimal totalDonado, 
                          LocalDateTime fechaPrimerAporte, LocalDateTime ultimaActualizacion) {
        this.usuarioId = usuarioId;
        this.tareasCompletadas = tareasCompletadas;
        this.totalDonado = totalDonado;
        this.fechaPrimerAporte = fechaPrimerAporte;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // Lógica de Negocio
    public void incrementarTareas() {
        this.tareasCompletadas++;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void agregarDonacion(BigDecimal monto) {
        if (this.totalDonado == null) this.totalDonado = BigDecimal.ZERO;
        this.totalDonado = this.totalDonado.add(monto);
        if (this.fechaPrimerAporte == null) this.fechaPrimerAporte = LocalDateTime.now();
        this.ultimaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public int getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(int tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }
    public BigDecimal getTotalDonado() { return totalDonado; }
    public void setTotalDonado(BigDecimal totalDonado) { this.totalDonado = totalDonado; }
    public LocalDateTime getFechaPrimerAporte() { return fechaPrimerAporte; }
    public void setFechaPrimerAporte(LocalDateTime fechaPrimerAporte) { this.fechaPrimerAporte = fechaPrimerAporte; }
    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}
