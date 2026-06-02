package es.refugio.frontend.service;

import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Historial Médico en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class HistorialMedicoService {

    private final BackendFeignClient backendClient;

    public PaginatedResponse<HistorialMedicoRecord> fetchPaginated(int page, int size) {
        try {
            return backendClient.getHistorialMedicoPaginated(page - 1, size);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<HistorialMedicoRecord> fetchAllHistoriales() {
        try {
            PaginatedResponse<HistorialMedicoRecord> res = backendClient.getHistorialMedico(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<HistorialMedicoRecord> fetchByAnimalId(Integer animalId) {
        try {
            return backendClient.getHistorialMedicoByAnimalId(animalId, 1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public HistorialMedicoRecord fetchHistorialById(Integer id) {
        try {
            return backendClient.getHistorialMedicoById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public void crearHistorial(Map<String, Object> body) {
        backendClient.createHistorialMedico(body);
    }

    public void editarHistorial(Integer id, Map<String, Object> body) {
        backendClient.updateHistorialMedico(id, body);
    }

    public void eliminarHistorial(Integer id) {
        backendClient.deleteHistorialMedico(id);
    }

    public List<AnimalRecord> fetchAllAnimales() {
        try {
            return backendClient.getAllAnimales(1000).items();
        } catch (Exception e) {
            return List.of();
        }
    }

    public AnimalRecord fetchAnimalById(Integer id) {
        try {
            return backendClient.getAnimalById(id);
        } catch (Exception e) {
            return null;
        }
    }
}
