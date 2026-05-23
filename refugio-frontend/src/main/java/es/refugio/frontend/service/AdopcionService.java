package es.refugio.frontend.service;

import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Adopciones en el Frontend.
 */
@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class AdopcionService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    public PaginatedResponse<AdopcionRecord> fetchPaginatedAdopciones(int page, int size, String q) {
        String path = "/v1/adopciones";
        if (q != null && !q.trim().isEmpty()) {
            path += "?q=" + q;
        }
        return helper.fetchPaginated(apiUrl + path, page, size, AdopcionRecord.class);
    }

    public List<AdopcionRecord> fetchAllAdopciones() {
        return helper.fetchList(apiUrl + "/v1/adopciones?size=1000", AdopcionRecord.class);
    }

    public AdopcionRecord fetchAdopcionById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/adopciones/" + id, AdopcionRecord.class);
    }

    public List<AdoptanteRecord> fetchAllAdoptantes() {
        return helper.fetchList(apiUrl + "/v1/adoptantes?size=1000", AdoptanteRecord.class);
    }

    public AdoptanteRecord fetchAdoptanteById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/adoptantes/" + id, AdoptanteRecord.class);
    }

    public List<PerfilLegalRecord> fetchAllPerfiles() {
        return helper.fetchList(apiUrl + "/v1/perfiles-legales?size=1000", PerfilLegalRecord.class);
    }

    public PerfilLegalRecord fetchPerfilByUsuarioId(Integer usuarioId) {
        return helper.fetchObject(apiUrl + "/v1/perfiles-legales/usuario/" + usuarioId, PerfilLegalRecord.class);
    }

    public List<UsuarioRecord> fetchAllUsuarios() {
        return helper.fetchList(authUrl + "/v1/usuarios?size=1000", UsuarioRecord.class);
    }

    public void crearAdopcion(Integer idPersona, Integer idAnimal, String estado, String fechaAdopcion) {
        Map<String, Object> body = new HashMap<>();
        body.put("adoptanteId", idPersona);
        body.put("animalId", idAnimal);
        body.put("estado", estado);
        
        String formattedDate = fechaAdopcion;
        if (formattedDate != null && !formattedDate.trim().isEmpty()) {
            if (!formattedDate.contains("T")) {
                formattedDate = formattedDate.trim() + "T00:00:00";
            }
        } else {
            formattedDate = java.time.LocalDateTime.now().toString();
        }
        body.put("fechaAdopcion", formattedDate);
        body.put("contrato", "Contrato formalizado");

        restTemplate.postForObject(apiUrl + "/v1/adopciones", body, Object.class);
    }

    public void editarAdopcion(Integer id, Integer idPersona, Integer idAnimal, String estado, String fechaAdopcion) {
        Map<String, Object> body = new HashMap<>();
        body.put("adoptanteId", idPersona);
        body.put("animalId", idAnimal);
        body.put("estado", estado);
        
        String formattedDate = fechaAdopcion;
        if (formattedDate != null && !formattedDate.trim().isEmpty()) {
            if (!formattedDate.contains("T")) {
                formattedDate = formattedDate.trim() + "T00:00:00";
            }
        } else {
            formattedDate = java.time.LocalDateTime.now().toString();
        }
        body.put("fechaAdopcion", formattedDate);

        restTemplate.put(apiUrl + "/v1/adopciones/" + id, body);
    }

    public void eliminarAdopcion(Integer id) {
        restTemplate.delete(apiUrl + "/v1/adopciones/" + id);
    }

    public ResponseEntity<byte[]> descargarContrato(Integer id) {
        return restTemplate.getForEntity(apiUrl + "/v1/reports/adopcion/" + id + "/contrato", byte[].class);
    }
}
