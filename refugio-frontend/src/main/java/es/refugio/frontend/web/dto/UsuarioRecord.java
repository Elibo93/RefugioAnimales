package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record UsuarioRecord(
        int id,
        String email,
        String username,
        String rol,
        LocalDateTime createdAt
) {}
