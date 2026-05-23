package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlobalAttributesService {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

    @Value("${backend.api.url}")
    private String apiUrl;

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchMe() {
        return restTemplate.getForObject(authUrl + "/v1/me", Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchPerfilLegal(Integer userId) {
        return restTemplate.getForObject(apiUrl + "/v1/perfiles-legales/usuario/" + userId, Map.class);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchMisSolicitudes() {
        return restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/mis-solicitudes", List.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> fetchPreferencias(Integer userId) {
        return restTemplate.getForObject(apiUrl + "/v1/preferencias/usuario/" + userId, Map.class);
    }

    public Long fetchPendingAdoptionsCount() {
        return restTemplate.getForObject(apiUrl + "/v1/solicitudes-adopcion/count/pendiente", Long.class);
    }

    public Long fetchPendingVolunteersCount() {
        return restTemplate.getForObject(apiUrl + "/v1/voluntarios/count/pendiente", Long.class);
    }
}
