package es.refugio.frontend.service;

import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Adopciones en el Frontend.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class AdopcionService {

    private final BackendFeignClient backendClient;
    private final AuthFeignClient authClient;

    public PaginatedResponse<AdopcionRecord> fetchPaginatedAdopciones(int page, int size, String q, String estado) {
        try {
            return backendClient.getAdopcionesPaginated(page - 1, size, q, estado);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<AdopcionRecord> fetchAllAdopciones() {
        try {
            PaginatedResponse<AdopcionRecord> res = backendClient.getAdopciones(1000);
            return res != null && res.items() != null ? res.items() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<AdopcionRecord> fetchAdopcionesByAnimalId(Integer animalId) {
        try {
            return backendClient.getAdopcionesByAnimalId(animalId, 1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public AdopcionRecord fetchAdopcionById(Integer id) {
        try {
            return backendClient.getAdopcionById(id);
        } catch (Exception e) {
            return null;
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

    public List<PerfilLegalRecord> fetchAllPerfiles() {
        try {
            return backendClient.getPerfilesLegales(1000);
        } catch (Exception e) {
            return List.of();
        }
    }

    public PerfilLegalRecord fetchPerfilByUsuarioId(Integer usuarioId) {
        try {
            return backendClient.getPerfilLegalByUsuarioId(usuarioId);
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
            formattedDate = LocalDateTime.now().toString();
        }
        body.put("fechaAdopcion", formattedDate);
        body.put("contrato", "Contrato formalizado");

        backendClient.createAdopcion(body);
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
            formattedDate = LocalDateTime.now().toString();
        }
        body.put("fechaAdopcion", formattedDate);

        backendClient.updateAdopcion(id, body);
    }

    public void eliminarAdopcion(Integer id) {
        backendClient.deleteAdopcion(id);
    }

    public ResponseEntity<byte[]> descargarContrato(Integer id) {
        return backendClient.descargarContrato(id);
    }
}
