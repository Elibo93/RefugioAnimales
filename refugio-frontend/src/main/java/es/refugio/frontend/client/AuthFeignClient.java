package es.refugio.frontend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import es.refugio.frontend.web.dto.UsuarioRecord;
import es.refugio.frontend.web.dto.PaginatedResponse;

import java.util.Map;

@FeignClient(name = "refugio-auth")
public interface AuthFeignClient {

    @PostMapping(value = "/api/v1/usuarios/publico")
    Map<String, Object> registrarUsuario(@RequestBody Map<String, Object> body);

    @PostMapping(value = "/api/v1/usuarios/internal/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password,
            @RequestHeader("X-Internal-Secret") String secret);

    @GetMapping("/api/v1/usuarios")
    PaginatedResponse<UsuarioRecord> getUsuarios(@RequestParam(value = "size", required = false) Integer size);

    @GetMapping("/api/v1/usuarios/{id}")
    UsuarioRecord getUsuarioById(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/me")
    UsuarioRecord getMe();

    @PostMapping("/api/v1/usuarios")
    Map<String, Object> createUserAuth(@RequestBody Map<String, Object> userBody);

    @PutMapping("/api/v1/usuarios/{id}")
    void updateUserAuth(@PathVariable("id") Integer id, @RequestBody Map<String, Object> userBody);

    @PostMapping(value = "/api/v1/usuarios/{id}/verificar-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Map<String, Object>> verificarPassword(@PathVariable("id") Integer id,
            @RequestParam("password") String password);

    @PutMapping("/api/v1/usuarios/{id}/password")
    void cambiarPassword(@PathVariable("id") Integer id, @RequestBody Map<String, String> body);

    @PutMapping("/api/v1/usuarios/{id}/rol")
    ResponseEntity<Map<String, Object>> actualizarRolUsuario(@PathVariable("id") Integer id,
            @RequestBody Map<String, String> body);

    @DeleteMapping("/api/v1/usuarios/{id}")
    void deleteUsuarioAuth(@PathVariable("id") Integer id);

    @DeleteMapping("/api/v1/usuarios/publico/rollback/{id}")
    void rollbackUsuarioAuth(@PathVariable("id") Integer id, @RequestHeader("X-Internal-Secret") String secret);

}
