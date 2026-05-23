package es.refugio.frontend.service;

import es.refugio.frontend.web.dto.AnimalRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;
import es.refugio.frontend.web.util.ViewControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Animales en el Frontend.
 * Abstrae las llamadas a la API (RestTemplate) y la lógica de filtrado.
 */
@Service
@RequiredArgsConstructor
public class AnimalService {

    private final RestTemplate restTemplate;
    private final ViewControllerHelper helper;

    @Value("${backend.api.url}")
    private String apiUrl;

    /**
     * Obtiene una lista paginada y filtrada de animales.
     */
    public PaginatedResponse<AnimalRecord> fetchPaginatedAnimals(int page, int size, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia, String q) {
        try {
            StringBuilder url = new StringBuilder("/v1/animales?");
            url.append("page=").append(page - 1).append("&size=").append(size).append("&");
            
            if (estado != null && !estado.isEmpty() && !"ALL".equalsIgnoreCase(estado)) 
                url.append("estado=").append(URLEncoder.encode(estado, StandardCharsets.UTF_8)).append("&");
            if (especie != null && !especie.isEmpty() && !"ALL".equalsIgnoreCase(especie)) 
                url.append("especie=").append(URLEncoder.encode(especie, StandardCharsets.UTF_8)).append("&");
            if (tamano != null && !tamano.isEmpty() && !"ALL".equalsIgnoreCase(tamano)) 
                url.append("tamano=").append(URLEncoder.encode(tamano, StandardCharsets.UTF_8)).append("&");
            if (sexo != null && !sexo.isEmpty() && !"ALL".equalsIgnoreCase(sexo)) 
                url.append("sexo=").append(URLEncoder.encode(sexo, StandardCharsets.UTF_8)).append("&");
            if (urgencia != null) url.append("urgencia=").append(urgencia).append("&");
            if (q != null && !q.trim().isEmpty()) 
                url.append("q=").append(URLEncoder.encode(q.trim(), StandardCharsets.UTF_8)).append("&");
            if (edad != null) edad.forEach(e -> url.append("edad=").append(URLEncoder.encode(e, StandardCharsets.UTF_8)).append("&"));

            return helper.fetchPaginated(apiUrl + url.toString(), page, size, AnimalRecord.class);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, page, size, 0, false, false);
        }
    }

    /**
     * Obtiene todos los animales (limitado a 1000).
     */
    public List<AnimalRecord> fetchAllAnimals() {
        return helper.fetchList(apiUrl + "/v1/animales?size=1000", AnimalRecord.class);
    }

    /**
     * Obtiene un animal por su ID.
     */
    public AnimalRecord fetchAnimalById(Integer id) {
        return helper.fetchObject(apiUrl + "/v1/animales/" + id, AnimalRecord.class);
    }

    /**
     * Obtiene las especies activas.
     */
    public List<String> fetchEspeciesActivas() {
        try {
            String[] arrEspecies = restTemplate.getForObject(apiUrl + "/v1/animales/especies", String[].class);
            return arrEspecies != null ? Arrays.asList(arrEspecies) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Obtiene los IDs de los animales favoritos de un usuario.
     */
    public List<Integer> fetchMisFavoritosIds(Integer usuarioId) {
        if (usuarioId == null) return List.of();
        try {
            Integer[] favs = restTemplate.getForObject(apiUrl + "/v1/animales/favoritos?usuarioId=" + usuarioId, Integer[].class);
            return favs != null ? Arrays.asList(favs) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Cuenta la cantidad de favoritos de un animal.
     */
    public int countFavoritos(Integer animalId) {
        try {
            Integer count = restTemplate.getForObject(apiUrl + "/v1/animales/" + animalId + "/favoritos/count", Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Alterna el estado de favorito de un animal para un usuario.
     */
    public Boolean toggleFavorito(Integer animalId, Integer usuarioId) {
        try {
            return restTemplate.postForObject(apiUrl + "/v1/animales/" + animalId + "/favorito?usuarioId=" + usuarioId, null, Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registra una visita a un animal.
     */
    public void registrarVisita(Integer animalId) {
        try {
            restTemplate.postForObject(apiUrl + "/v1/animales/" + animalId + "/visitas", null, Object.class);
        } catch (Exception ignored) {}
    }

    /**
     * Crea un animal.
     */
    public void crearAnimal(Map<String, Object> body, MultipartFile fotoArchivo) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> animalEntity = new HttpEntity<>(body, jsonHeaders);
        parts.add("animal", animalEntity);
        
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            parts.add("fotoArchivo", fotoArchivo.getResource());
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);

        restTemplate.postForObject(apiUrl + "/v1/animales", requestEntity, Object.class);
    }

    /**
     * Edita un animal.
     */
    public void editarAnimal(Integer id, Map<String, Object> body, MultipartFile fotoArchivo) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> animalEntity = new HttpEntity<>(body, jsonHeaders);
        parts.add("animal", animalEntity);
        
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            parts.add("fotoArchivo", fotoArchivo.getResource());
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);

        restTemplate.put(apiUrl + "/v1/animales/" + id, requestEntity);
    }

    /**
     * Elimina un animal.
     */
    public void eliminarAnimal(Integer id) {
        restTemplate.delete(apiUrl + "/v1/animales/" + id);
    }
}
