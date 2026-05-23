package es.refugio.frontend.service;

import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Solicitudes de Adopción en el Frontend.
 */
@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class SolicitudAdopcionService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    public PaginatedResponse<SolicitudAdopcionRecord> fetchPaginatedSolicitudes(int page, int size) {
        return helper.fetchPaginated(apiUrl + "/v1/solicitudes-adopcion", page, size, SolicitudAdopcionRecord.class);
    }

    public List<SolicitudAdopcionRecord> fetchAllSolicitudes() {
        return helper.fetchList(apiUrl + "/v1/solicitudes-adopcion?size=1000", SolicitudAdopcionRecord.class);
    }

    public SolicitudAdopcionRecord fetchSolicitudById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/solicitudes-adopcion/" + id, SolicitudAdopcionRecord.class);
    }

    public void crearSolicitud(Integer animalId, Integer adoptanteId, String estado, String comentario, String comentarioAdmin, java.time.LocalDateTime fecha) {
        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        body.put("fecha", fecha != null ? fecha.toString() : java.time.LocalDateTime.now().toString());
        
        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion", body, Object.class);
    }

    public void editarSolicitud(Integer id, Integer animalId, Integer adoptanteId, String estado, String comentario, String comentarioAdmin, java.time.LocalDateTime fecha) {
        Map<String, Object> body = new HashMap<>();
        body.put("animalId", animalId);
        body.put("adoptanteId", adoptanteId);
        body.put("estado", estado);
        body.put("comentario", comentario);
        body.put("comentarioAdmin", comentarioAdmin);
        if (fecha != null) {
            body.put("fecha", fecha.toString());
        }
        
        restTemplate.put(apiUrl + "/v1/solicitudes-adopcion/" + id, body);
    }

    public void actualizarEstadoSolicitud(Integer id, String estado) {
        SolicitudAdopcionRecord record = fetchSolicitudById(id);
        if (record != null) {
            editarSolicitud(id, record.animalId(), record.adoptanteId(), estado, record.comentario(), record.comentarioAdmin(), record.fecha());
        }
    }

    public void eliminarSolicitud(Integer id) {
        restTemplate.delete(apiUrl + "/v1/solicitudes-adopcion/" + id);
    }

    public void aprobarSolicitud(Integer id) {
        restTemplate.postForEntity(apiUrl + "/v1/solicitudes-adopcion/" + id + "/aprobar", null, Object.class);
    }

    public void rechazarSolicitud(Integer id) {
        restTemplate.postForEntity(apiUrl + "/v1/solicitudes-adopcion/" + id + "/rechazar", null, Object.class);
    }

    public List<AnimalRecord> fetchAllAnimales() {
        return helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
    }

    public AnimalRecord fetchAnimalById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/animales/" + id, AnimalRecord.class);
    }

    public List<AdoptanteRecord> fetchAllAdoptantes() {
        return helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class);
    }

    public AdoptanteRecord fetchAdoptanteById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/adoptantes/" + id, AdoptanteRecord.class);
    }
    
    public AdoptanteRecord fetchAdoptanteByUsuarioId(Integer usuarioId) {
        return helper.fetchObject(apiUrl + "/v1/adoptantes/usuario/" + usuarioId, AdoptanteRecord.class);
    }

    public List<UsuarioRecord> fetchAllUsuarios() {
        return helper.fetchList(authUrl + "/v1/usuarios?size=1000", UsuarioRecord.class);
    }

    public List<PerfilLegalRecord> fetchAllPerfilesLegales() {
        return helper.fetchList(apiUrl + "/v1/perfiles-legales?size=1000", PerfilLegalRecord.class);
    }

    public PerfilLegalRecord fetchPerfilLegalByUsuarioId(Integer usuarioId) {
        return helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + usuarioId, PerfilLegalRecord.class);
    }

    public List<AdopcionRecord> fetchAllAdopciones() {
        return helper.fetchList(apiUrl + "/v1/adopciones?size=1000", AdopcionRecord.class);
    }

    public List<AdopcionRecord> fetchAdopcionesByAdoptanteId(Integer adoptanteId) {
        return helper.fetchList(apiUrl + "/v1/adopciones/adoptante/" + adoptanteId + "?size=1000", AdopcionRecord.class);
    }

    public ResponseEntity<byte[]> descargarPdfSolicitud(Integer id) {
        return restTemplate.getForEntity(apiUrl + "/v1/reports/solicitud/" + id, byte[].class);
    }

    public UsuarioRecord fetchMe() {
        return helper.fetchObject(authUrl + "/v1/me", UsuarioRecord.class);
    }

    public void convertirYAdopcion(Map<String, Object> bodySolicitud) {
        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/convertir-y-adopcion", bodySolicitud, Object.class);
    }

    public ResponseEntity<Map<String, Object>> actualizarRolUsuario(Integer usuarioId, String nuevoRol) {
        Map<String, String> patchBody = new HashMap<>();
        patchBody.put("rol", nuevoRol);
        return restTemplate.exchange(authUrl + "/v1/usuarios/" + usuarioId + "/rol", org.springframework.http.HttpMethod.PUT, new org.springframework.http.HttpEntity<>(patchBody), new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public void crearAdopcionDirecta(Map<String, Object> body) {
        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/directa", body, Object.class);
    }

    public Map<?, ?> registrarUsuarioPublico(Map<String, Object> userBody) {
        return restTemplate.postForObject(authUrl + "/v1/usuarios/publico", userBody, Map.class);
    }

    public void registrarYAdopcionPublico(Map<String, Object> bodySolicitud) {
        restTemplate.postForObject(apiUrl + "/v1/solicitudes-adopcion/publico/registro-y-adopcion", bodySolicitud, Object.class);
    }

    public ResponseEntity<String> loginPost(String email, String password) {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        String loginBody = "username=" + email + "&password=" + password;
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(loginBody, headers);
        String authBaseUrl = authUrl.substring(0, authUrl.lastIndexOf("/api"));
        String loginUrl = authBaseUrl + "/login-post";
        return restTemplate.postForEntity(loginUrl, entity, String.class);
    }
}
