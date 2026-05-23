package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final RestTemplate restTemplate;

    @Value("${backend.api.url}")
    private String apiUrl;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchMisNotificaciones() {
        String url = apiUrl + "/v1/notificaciones/me";
        Object[] arr = restTemplate.getForObject(url, Object[].class);
        if (arr == null) return List.of();
        
        return Arrays.stream(arr)
                .filter(o -> o instanceof Map)
                .map(o -> (Map<String, Object>) o)
                .collect(Collectors.toList());
    }

    public void marcarComoLeida(Integer id) {
        restTemplate.exchange(apiUrl + "/v1/notificaciones/" + id + "/leer", HttpMethod.PUT, null, Void.class);
    }

    public Long contarNoLeidas() {
        return restTemplate.getForObject(apiUrl + "/v1/notificaciones/me/count", Long.class);
    }

    public void eliminarNotificacion(Integer id) {
        restTemplate.delete(apiUrl + "/v1/notificaciones/" + id);
    }
}
