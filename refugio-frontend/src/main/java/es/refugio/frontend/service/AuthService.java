package es.refugio.frontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import es.refugio.frontend.client.AuthFeignClient;
import java.util.Map;

/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Auth.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthFeignClient authClient;

    public Map<String, Object> registrarUsuario(Map<String, Object> body) {
        return authClient.registrarUsuario(body);
    }

    @Value("${refugio.internal.secret}")
    private String internalSecret;

    public ResponseEntity<String> login(String email, String password) {
        return authClient.login(email, password, internalSecret);
    }
}

