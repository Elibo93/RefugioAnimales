package es.refugio.frontend.service;

import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Donaciones en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class DonacionService {

    private final BackendFeignClient backendClient;
    private final AuthFeignClient authClient;

    public PaginatedResponse<DonacionRecord> fetchPaginated(int page, int size) {
        try {
            return backendClient.getDonacionesPaginated(page - 1, size);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<DonacionRecord> fetchAllDonaciones() {
        try {
            PaginatedResponse<DonacionRecord> res = backendClient.getDonaciones(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public DonacionRecord fetchDonacionById(Integer id) {
        try {
            return backendClient.getDonacionById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<UsuarioRecord> fetchUsuarios() {
        try {
            PaginatedResponse<UsuarioRecord> res = authClient.getUsuarios(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Map<String, Object>> fetchObjetivos() {
        try {
            return backendClient.getObjetivosDonacion(1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public Double fetchTotalDinero() {
        try {
            Double total = backendClient.getTotalDineroDonaciones();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void crearDonacion(Map<String, Object> body) {
        backendClient.createDonacion(body);
    }

    public void editarDonacion(Integer id, Map<String, Object> body) {
        backendClient.updateDonacion(id, body);
    }

    public void eliminarDonacion(Integer id) {
        backendClient.deleteDonacion(id);
    }

    public void crearObjetivo(Map<String, Object> body) {
        backendClient.createObjetivoDonacion(body);
    }
}
