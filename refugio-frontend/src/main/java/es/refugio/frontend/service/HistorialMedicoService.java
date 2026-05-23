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
 * Servicio para gestionar las operaciones relacionadas con Historial Médico en el Frontend.
 */
@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Historial Medico.
 *
 * @author Elisabeth
 * @author Diego
 */
public class HistorialMedicoService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    public PaginatedResponse<HistorialMedicoRecord> fetchPaginated(int page, int size) {
        return helper.fetchPaginated(apiUrl + "/v1/historial-medico", page, size, HistorialMedicoRecord.class);
    }

    public List<HistorialMedicoRecord> fetchAllHistoriales() {
        return helper.fetchList(apiUrl + "/v1/historial-medico?size=1000", HistorialMedicoRecord.class);
    }

    public List<HistorialMedicoRecord> fetchByAnimalId(Integer animalId) {
        return helper.fetchList(apiUrl + "/v1/historial-medico/animal/" + animalId + "?size=1000", HistorialMedicoRecord.class);
    }

    public HistorialMedicoRecord fetchHistorialById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/historial-medico/" + id, HistorialMedicoRecord.class);
    }

    public void crearHistorial(Map<String, Object> body) {
        restTemplate.postForObject(apiUrl + "/v1/historial-medico", body, Object.class);
    }

    public void editarHistorial(Integer id, Map<String, Object> body) {
        restTemplate.put(apiUrl + "/v1/historial-medico/" + id, body);
    }

    public void eliminarHistorial(Integer id) {
        restTemplate.delete(apiUrl + "/v1/historial-medico/" + id);
    }

    public List<AnimalRecord> fetchAllAnimales() {
        return helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
    }

    public AnimalRecord fetchAnimalById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/animales/" + id, AnimalRecord.class);
    }
}
