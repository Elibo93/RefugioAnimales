package es.refugio.frontend.web.dto;

public record VoluntarioEncontradoRecord(
    Integer id,
    String nombre,
    String apellido,
    String email,
    String username
) {}
