package es.refugio.refugio.domain.model.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.enums.CategoriaLogro;
import es.refugio.refugio.domain.model.gamificacion.enums.RarezaLogro;
import es.refugio.refugio.domain.model.gamificacion.enums.RequisitoTipo;

import java.math.BigDecimal;

public class Logro {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private CategoriaLogro categoria;
    private RequisitoTipo requisitoTipo;
    private BigDecimal requisitoValor;
    private String iconoLucide;
    private RarezaLogro rareza;

    public Logro() {}

    public Logro(Long id, String codigo, String nombre, String descripcion, CategoriaLogro categoria, 
                 RequisitoTipo requisitoTipo, BigDecimal requisitoValor, String iconoLucide, RarezaLogro rareza) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.requisitoTipo = requisitoTipo;
        this.requisitoValor = requisitoValor;
        this.iconoLucide = iconoLucide;
        this.rareza = rareza;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public CategoriaLogro getCategoria() { return categoria; }
    public void setCategoria(CategoriaLogro categoria) { this.categoria = categoria; }
    public RequisitoTipo getRequisitoTipo() { return requisitoTipo; }
    public void setRequisitoTipo(RequisitoTipo requisitoTipo) { this.requisitoTipo = requisitoTipo; }
    public BigDecimal getRequisitoValor() { return requisitoValor; }
    public void setRequisitoValor(BigDecimal requisitoValor) { this.requisitoValor = requisitoValor; }
    public String getIconoLucide() { return iconoLucide; }
    public void setIconoLucide(String iconoLucide) { this.iconoLucide = iconoLucide; }
    public RarezaLogro getRareza() { return rareza; }
    public void setRareza(RarezaLogro rareza) { this.rareza = rareza; }
}
