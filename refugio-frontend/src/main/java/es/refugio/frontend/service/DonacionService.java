package es.refugio.frontend.service;

import es.refugio.frontend.web.dto.*;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Donaciones en el Frontend.
 */
@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Donacion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DonacionService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    @Value("${auth.api.url}")
    private String authUrl;

    public PaginatedResponse<DonacionRecord> fetchPaginated(int page, int size) {
        return helper.fetchPaginated(apiUrl + "/v1/donaciones", page, size, DonacionRecord.class);
    }

    public List<DonacionRecord> fetchAllDonaciones() {
        return helper.fetchList(apiUrl + "/v1/donaciones?size=1000", DonacionRecord.class);
    }

    public DonacionRecord fetchDonacionById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/donaciones/" + id, DonacionRecord.class);
    }

    public List<UsuarioRecord> fetchUsuarios() {
        return helper.fetchList(authUrl + "/v1/usuarios?size=1000", UsuarioRecord.class);
    }

    @SuppressWarnings("rawtypes")
    public List<Map> fetchObjetivos() {
        return helper.fetchList(apiUrl + "/v1/objetivos-donacion?size=1000", Map.class);
    }

    public Double fetchTotalDinero() {
        try {
            Double total = restTemplate.getForObject(apiUrl + "/v1/donaciones/total", Double.class);
            return total != null ? total : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void crearDonacion(Map<String, Object> body) {
        restTemplate.postForObject(apiUrl + "/v1/donaciones", body, Object.class);
    }

    public void editarDonacion(Integer id, Map<String, Object> body) {
        restTemplate.put(apiUrl + "/v1/donaciones/" + id, body);
    }

    public void eliminarDonacion(Integer id) {
        restTemplate.delete(apiUrl + "/v1/donaciones/" + id);
    }

    public void crearObjetivo(Map<String, Object> body) {
        restTemplate.postForObject(apiUrl + "/v1/objetivos-donacion", body, Object.class);
    }
}
