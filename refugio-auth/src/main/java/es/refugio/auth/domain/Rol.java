package es.refugio.auth.domain;

public enum Rol {
    ROLE_ADMIN,
    ROLE_VOLUNTARIO,
    ROLE_ADOPTANTE,
    ROLE_VOLUNTARIO_ADOPTANTE,
    ROLE_PUBLICO,
    
    // Nombres sin prefijo (para flexibilidad)
    ADMIN,
    VOLUNTARIO,
    ADOPTANTE,
    VOLUNTARIO_ADOPTANTE,
    PUBLICO;
}
