package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

    @SuppressWarnings("unchecked")
    public Map<String, Object> registrarUsuario(Map<String, Object> body) {
        return restTemplate.postForObject(authUrl + "/v1/usuarios/publico", body, Map.class);
    }

    public ResponseEntity<String> login(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String loginBody = "username=" + email + "&password=" + password;
        HttpEntity<String> entity = new HttpEntity<>(loginBody, headers);
        
        String authBaseUrl = authUrl.substring(0, authUrl.lastIndexOf("/api"));
        String loginUrl = authBaseUrl + "/login-post";
        
        return restTemplate.postForEntity(loginUrl, entity, String.class);
    }
}
