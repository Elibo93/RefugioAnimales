package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PreferenciaService {

    private final RestTemplate restTemplate;

    @Value("${backend.api.url}")
    private String apiUrl;

    public void guardarPreferencias(Map<String, Object> payload) {
        restTemplate.postForObject(apiUrl + "/v1/preferencias", payload, Map.class);
    }
}
