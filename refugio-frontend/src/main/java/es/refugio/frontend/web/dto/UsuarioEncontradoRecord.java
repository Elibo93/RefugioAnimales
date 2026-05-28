package es.refugio.frontend.web.dto;

public record UsuarioEncontradoRecord(
    Integer id,
    String username,
    String email,
    String rol,
    String nombre,
    String apellido,
    Integer adoptanteId,
    boolean yaRegistrado
) {}
