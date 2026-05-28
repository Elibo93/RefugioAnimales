package es.refugio.frontend.service;

import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Adoptantes en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class AdoptanteService {

    private final BackendFeignClient backendClient;
    private final AuthFeignClient authClient;

    public PaginatedResponse<AdoptanteRecord> fetchPaginatedAdoptantes(int page, int size, String q) {
        try {
            return backendClient.getAdoptantesPaginated(page - 1, size, q);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<AdoptanteRecord> fetchAllAdoptantes() {
        try {
            PaginatedResponse<AdoptanteRecord> res = backendClient.getAdoptantes(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public AdoptanteRecord fetchAdoptanteById(Integer id) {
        try {
            return backendClient.getAdoptanteById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<UsuarioRecord> fetchAllUsuarios() {
        try {
            PaginatedResponse<UsuarioRecord> res = authClient.getUsuarios(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public UsuarioRecord fetchUsuarioById(Integer id) {
        try {
            return authClient.getUsuarioById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<PerfilLegalRecord> fetchAllPerfilesLegales() {
        try {
            return backendClient.getPerfilesLegales(1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public PerfilLegalRecord fetchPerfilLegalByUsuarioId(Integer usuarioId) {
        try {
            return backendClient.getPerfilLegalByUsuarioId(usuarioId);
        } catch (Exception e) {
            return null;
        }
    }

    public void crearAdoptanteYPerfil(Integer usuarioId, String nombre, String apellido, String dni, String direccion, String telefono, String fechaNacimiento, String estadoValidacion) {
        // 1. Crear/Actualizar PerfilLegal (Fuente de verdad para identidad)
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        backendClient.createPerfilLegal(bodyPerfil);

        // 2. Crear Perfil de Adoptante (Rol operativo)
        Map<String, Object> bodyAdoptante = new HashMap<>();
        bodyAdoptante.put("usuarioId", usuarioId);
        if (estadoValidacion != null) {
            bodyAdoptante.put("estadoValidacion", estadoValidacion);
        }
        backendClient.createAdoptante(bodyAdoptante);
    }

    public void editarAdoptanteYPerfil(Integer id, Integer usuarioId, String nombre, String apellido, String dni, String direccion, String telefono, String fechaNacimiento, String estadoValidacion) {
        // 1. Actualizar Adoptante
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        if (estadoValidacion != null) {
            body.put("estadoValidacion", estadoValidacion);
        }
        backendClient.updateAdoptante(id, body);

        // 2. Actualizar PerfilLegal
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        backendClient.createPerfilLegal(bodyPerfil); // Asumiendo que el backend maneja upsert basado en usuarioId
    }

    public void eliminarAdoptante(Integer id) {
        backendClient.deleteAdoptante(id);
    }

    public void aprobarAdoptante(Integer id) {
        backendClient.approveAdoptante(id);
    }

    public void rechazarAdoptante(Integer id) {
        backendClient.rejectAdoptante(id);
    }
}

