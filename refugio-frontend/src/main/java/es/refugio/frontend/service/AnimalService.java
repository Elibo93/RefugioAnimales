package es.refugio.frontend.service;

import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.AnimalRecord;
import es.refugio.frontend.web.dto.AnimalRequest;
import es.refugio.frontend.web.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con Animales en el Frontend.
 * Abstrae las llamadas a la API mediante Feign Client.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class AnimalService {

    private final BackendFeignClient backendClient;

    public PaginatedResponse<AnimalRecord> fetchPaginatedAnimals(int page, int size, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia, String q) {
        try {
            return backendClient.getAnimales(page - 1, size, estado, especie, tamano, edad, sexo, urgencia, q);
        } catch (Exception e) {
            return new PaginatedResponse<>(List.of(), 0, 0, page, size, false, false);
        }
    }

    public List<AnimalRecord> fetchAllAnimals() {
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

    public List<String> fetchEspeciesActivas() {
        try {
            return backendClient.getEspeciesActivas();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Integer> fetchMisFavoritosIds(Integer usuarioId) {
        if (usuarioId == null) return List.of();
        try {
            return backendClient.getMisFavoritosIds(usuarioId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public int countFavoritos(Integer animalId) {
        try {
            return backendClient.countFavoritos(animalId);
        } catch (Exception e) {
            return 0;
        }
    }

    public Boolean toggleFavorito(Integer animalId, Integer usuarioId) {
        try {
            return backendClient.toggleFavorito(animalId, usuarioId);
        } catch (Exception e) {
            return false;
        }
    }

    public void registrarVisita(Integer animalId) {
        try {
            backendClient.registrarVisita(animalId);
        } catch (Exception ignored) {}
    }

    public void crearAnimal(Map<String, Object> body, MultipartFile fotoArchivo) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            String animalJson = objectMapper.writeValueAsString(mapToAnimalRequest(body));
            backendClient.crearAnimal(animalJson, fotoArchivo);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando AnimalRequest", e);
        }
    }

    public void editarAnimal(Integer id, Map<String, Object> body, MultipartFile fotoArchivo) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            String animalJson = objectMapper.writeValueAsString(mapToAnimalRequest(body));
            backendClient.editarAnimal(id, animalJson, fotoArchivo);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando AnimalRequest", e);
        }
    }

    private AnimalRequest mapToAnimalRequest(Map<String, Object> body) {
        return new AnimalRequest(
            (String) body.get("nombre"),
            (String) body.get("especie"),
            (String) body.get("especiePersonalizada"),
            (String) body.get("raza"),
            (String) body.get("sexo"),
            (String) body.get("chipId"),
            (String) body.get("estado"),
            body.get("edad") instanceof Number n ? n.intValue() : null,
            (String) body.get("tamano"),
            (String) body.get("descripcion"),
            (String) body.get("foto"),
            body.get("peso") instanceof Number n ? n.doubleValue() : null,
            body.get("nivelEnergia") instanceof Number n ? n.intValue() : null,
            body.get("urgencia") instanceof Boolean b ? b : null,
            (String) body.get("fechaIngreso")
        );
    }

    public void eliminarAnimal(Integer id) {
        backendClient.eliminarAnimal(id);
    }
}

