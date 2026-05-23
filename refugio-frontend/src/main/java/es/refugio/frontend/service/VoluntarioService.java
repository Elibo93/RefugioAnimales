package es.refugio.frontend.service;

import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Voluntarios en el Frontend.
 */
@Service
@RequiredArgsConstructor
public class VoluntarioService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    public PaginatedResponse<VoluntarioRecord> fetchPaginatedVoluntarios(int page, int size, String q) {
        String url = apiUrl + "/v1/voluntarios";
        if (q != null && !q.trim().isEmpty()) {
            url += "?q=" + q;
        }
        return helper.fetchPaginated(url, page, size, VoluntarioRecord.class);
    }

    public List<VoluntarioRecord> fetchAllVoluntarios() {
        return helper.fetchList(apiUrl + "/v1/voluntarios", VoluntarioRecord.class);
    }

    public VoluntarioRecord fetchVoluntarioById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/voluntarios/" + id, VoluntarioRecord.class);
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

    public List<TareaRecord> fetchTareasByVoluntario(Integer voluntarioId) {
        return helper.fetchList(apiUrl + "/v1/tareas/voluntario/" + voluntarioId, TareaRecord.class);
    }

    public List<Map<String, Object>> fetchDisponibilidad(Integer voluntarioId) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    apiUrl + "/v1/voluntarios/" + voluntarioId + "/disponibilidad",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public TareaRecord fetchTareaById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/tareas/" + id, TareaRecord.class);
    }

    public VoluntarioRecord fetchVoluntarioByUsuarioId(Integer usuarioId) {
        return helper.fetchObject(apiUrl + "/v1/voluntarios/usuario/" + usuarioId, VoluntarioRecord.class);
    }

    public UsuarioRecord fetchMe() {
        return helper.fetchObject(authUrl + "/v1/me", UsuarioRecord.class);
    }

    public UsuarioRecord crearUsuario(String email, String contrasena, String rol) {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("email", email);
        userBody.put("contrasena", contrasena);
        userBody.put("rol", rol);
        return restTemplate.postForObject(authUrl + "/v1/usuarios", userBody, UsuarioRecord.class);
    }

    public List<VoluntarioRecord> fetchVoluntariosPendientes() {
        return helper.fetchList(apiUrl + "/v1/voluntarios/pendientes", VoluntarioRecord.class);
    }

    public void crearVoluntarioYPerfil(Integer usuarioId, String nombre, String apellido, String dni, String direccion, String telefono, String fechaNacimiento, String especialidad, String disponibilidad) {
        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

        Map<String, Object> bodyVoluntario = new HashMap<>();
        bodyVoluntario.put("usuarioId", usuarioId);
        bodyVoluntario.put("disponibilidad", disponibilidad);
        bodyVoluntario.put("especialidad", especialidad);
        restTemplate.postForObject(apiUrl + "/v1/voluntarios", bodyVoluntario, Object.class);
    }

    public void editarVoluntarioYPerfil(Integer id, Integer usuarioId, String nombre, String apellido, String email, String dni, String direccion, String telefono, String fechaNacimiento, String especialidad, String disponibilidad) {
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        if (disponibilidad != null) body.put("disponibilidad", disponibilidad);
        if (especialidad != null) body.put("especialidad", especialidad);
        restTemplate.put(apiUrl + "/v1/voluntarios/" + id, body);

        Map<String, Object> bodyPerfil = new HashMap<>();
        bodyPerfil.put("usuarioId", usuarioId);
        bodyPerfil.put("nombre", nombre);
        bodyPerfil.put("apellido", apellido);
        bodyPerfil.put("dni", dni);
        bodyPerfil.put("direccion", direccion);
        bodyPerfil.put("telefono", (telefono != null && !telefono.isEmpty()) ? telefono : "000000000");
        bodyPerfil.put("fechaNacimiento", fechaNacimiento);
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", bodyPerfil, Object.class);

        // Si se nos proporciona un email, actualizamos el usuario (solo si es necesario, ignoramos si hay error)
        if (email != null && !email.trim().isEmpty()) {
            try {
                UsuarioRecord user = fetchUsuarioById(usuarioId);
                if (user != null) {
                    Map<String, Object> bodyUser = new HashMap<>();
                    bodyUser.put("id", user.id());
                    bodyUser.put("username", user.username());
                    bodyUser.put("email", email);
                    bodyUser.put("rol", user.rol());
                    bodyUser.put("contrasena", "secret_placeholder");
                    restTemplate.put(authUrl + "/v1/usuarios/" + usuarioId, bodyUser);
                }
            } catch (Exception ignored) {}
        }
    }

    public void eliminarVoluntario(Integer id) {
        restTemplate.delete(apiUrl + "/v1/voluntarios/" + id);
    }

    public void aprobarVoluntario(Integer id) {
        restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/aprobar", null, Object.class);
    }

    public void rechazarVoluntario(Integer id) {
        restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/rechazar", null, Object.class);
    }

    public void aprobarSolicitudVoluntario(Integer id) {
        restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/aprobar", null, Void.class);
    }

    public void rechazarSolicitudVoluntario(Integer id) {
        restTemplate.postForEntity(apiUrl + "/v1/voluntarios/" + id + "/rechazar", null, Void.class);
    }
    
    public void addDisponibilidad(Integer voluntarioId, String diaSemana, String horaInicio, String horaFin) {
        Map<String, Object> body = new HashMap<>();
        body.put("diaSemana", diaSemana);
        body.put("horaInicio", horaInicio);
        body.put("horaFin", horaFin);
        restTemplate.postForObject(apiUrl + "/v1/voluntarios/" + voluntarioId + "/disponibilidad", body, Object.class);
    }
    
    public void deleteDisponibilidad(Integer voluntarioId, Integer disponibilidadId) {
        restTemplate.delete(apiUrl + "/v1/voluntarios/" + voluntarioId + "/disponibilidad/" + disponibilidadId);
    }
}
