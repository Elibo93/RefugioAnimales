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
 * Servicio para gestionar las operaciones relacionadas con Tareas en el Frontend.
 */
@Service
@RequiredArgsConstructor
public class TareaService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    public List<TareaRecord> fetchAllTareas() {
        return helper.fetchList(apiUrl + "/v1/tareas?size=9999", TareaRecord.class);
    }

    public TareaRecord fetchTareaById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/tareas/" + id, TareaRecord.class);
    }

    public void crearTarea(Map<String, Object> body) {
        restTemplate.postForObject(apiUrl + "/v1/tareas", body, Object.class);
    }

    public void editarTarea(Integer id, Map<String, Object> body) {
        restTemplate.put(apiUrl + "/v1/tareas/" + id, body);
    }

    public void eliminarTarea(Integer id) {
        restTemplate.delete(apiUrl + "/v1/tareas/" + id);
    }

    public void actualizarEstadoTarea(Integer id, String estado) {
        Map<String, Object> body = Map.of("estado", estado);
        restTemplate.put(apiUrl + "/v1/tareas/" + id, body);
    }

    public org.springframework.http.ResponseEntity<byte[]> descargarPdfTarea(Integer id) {
        return restTemplate.exchange(
            apiUrl + "/v1/reports/tarea/" + id,
            org.springframework.http.HttpMethod.GET,
            null,
            byte[].class
        );
    }

    public String fetchHtmlTarea(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/tareas/" + id + "/html", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchHistorial(Integer id) {
        return restTemplate.getForObject(apiUrl + "/v1/tareas/" + id + "/historial", List.class);
    }
}
