package es.refugio.frontend.service;

import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Adoptantes en el Frontend.
 */
@Service
@RequiredArgsConstructor
public class AdoptanteService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    public PaginatedResponse<AdoptanteRecord> fetchPaginatedAdoptantes(int page, int size, String q) {
        String url = apiUrl + "/v1/adoptantes";
        if (q != null && !q.trim().isEmpty()) {
            url += "?q=" + q;
        }
        return helper.fetchPaginated(url, page, size, AdoptanteRecord.class);
    }

    public List<AdoptanteRecord> fetchAllAdoptantes() {
        return helper.fetchList(apiUrl + "/v1/adoptantes", AdoptanteRecord.class);
    }

    public AdoptanteRecord fetchAdoptanteById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/adoptantes/" + id, AdoptanteRecord.class);
    }

    public List<UsuarioRecord> fetchAllUsuarios() {
        return helper.fetchList(authUrl + "/v1/usuarios", UsuarioRecord.class);
    }

    public UsuarioRecord fetchUsuarioById(Integer id) {
        return helper.fetchObject(authUrl + "/v1/usuarios/" + id, UsuarioRecord.class);
    }

    public List<PerfilLegalRecord> fetchAllPerfilesLegales() {
        return helper.fetchList(apiUrl + "/v1/perfiles-legales", PerfilLegalRecord.class);
    }

    public PerfilLegalRecord fetchPerfilLegalByUsuarioId(Integer usuarioId) {
        return helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + usuarioId, PerfilLegalRecord.class);
    }

    public void crearAdoptanteYPerfil(Integer usuarioId, String nombre, String apellido, String dni, String direccion, String telefono, String fechaNacimiento) {
        // 1. Crear/Actualizar PerfilLegal (Fuente de verdad para identidad)
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

        // 2. Crear Perfil de Adoptante (Rol operativo)
        Map<String, Object> bodyAdoptante = new HashMap<>();
        bodyAdoptante.put("usuarioId", usuarioId);
        restTemplate.postForObject(apiUrl + "/v1/adoptantes", bodyAdoptante, Object.class);
    }

    public void editarAdoptanteYPerfil(Integer id, Integer usuarioId, String nombre, String apellido, String dni, String direccion, String fechaNacimiento, String estadoValidacion) {
        // 1. Actualizar Adoptante
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        if (estadoValidacion != null) {
            body.put("estadoValidacion", estadoValidacion);
        }
        restTemplate.put(apiUrl + "/v1/adoptantes/" + id, body);

        // 2. Actualizar PerfilLegal
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);
    }

    public void eliminarAdoptante(Integer id) {
        restTemplate.delete(apiUrl + "/v1/adoptantes/" + id);
    }

    public void aprobarAdoptante(Integer id) {
        restTemplate.patchForObject(apiUrl + "/v1/adoptantes/" + id + "/approve", null, Object.class);
    }

    public void rechazarAdoptante(Integer id) {
        restTemplate.patchForObject(apiUrl + "/v1/adoptantes/" + id + "/reject", null, Object.class);
    }
}
