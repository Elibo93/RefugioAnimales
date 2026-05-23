package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Usuario.
 *
 * @author Elisabeth
 * @author Diego
 */
public class UsuarioService {

    private final RestTemplate restTemplate;

    @Value("${auth.api.url}")
    private String authUrl;

    @Value("${backend.api.url}")
    private String apiUrl;

    public Map<?, ?> createUserAuth(Map<String, Object> userBody) {
        return restTemplate.postForObject(authUrl + "/v1/usuarios", userBody, Map.class);
    }

    public void createPerfilLegal(Map<String, Object> legalBody) {
        restTemplate.postForObject(apiUrl + "/v1/perfiles-legales", legalBody, Object.class);
    }

    public void updateUserAuth(Integer id, Map<String, Object> userBody) {
        restTemplate.put(authUrl + "/v1/usuarios/" + id, userBody);
    }

    public ResponseEntity<?> verificarPassword(Integer id, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("password", password != null ? password.trim() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity(authUrl + "/v1/usuarios/" + id + "/verificar-password", req, Map.class);
    }

    public void cambiarPassword(Integer id, Map<String, String> body) {
        restTemplate.put(authUrl + "/v1/usuarios/" + id + "/password", body);
    }

    public void deletePerfilLegal(Integer id) {
        restTemplate.delete(apiUrl + "/v1/perfiles-legales/usuario/" + id);
    }

    public void deleteUsuarioAuth(Integer id) {
        restTemplate.delete(authUrl + "/v1/usuarios/" + id);
    }

    public Map fetchMetricasGamificacion(Integer id) {
        return restTemplate.getForObject(apiUrl + "/v1/gamificacion/metricas/usuario/" + id, Map.class);
    }

    public Map<?, ?> createAdoptante(Map<String, Object> adoptanteReq) {
        return restTemplate.postForObject(apiUrl + "/v1/adoptantes", adoptanteReq, Map.class);
    }
}
