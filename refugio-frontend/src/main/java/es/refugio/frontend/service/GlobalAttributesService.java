package es.refugio.frontend.service;

import es.refugio.frontend.client.AuthFeignClient;
import es.refugio.frontend.client.BackendFeignClient;
import es.refugio.frontend.web.dto.UsuarioRecord;
import es.refugio.frontend.web.dto.PerfilLegalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Global Attributes.
 *
 * @author Elisabeth
 * @author Diego
 */
@Service
@RequiredArgsConstructor
public class GlobalAttributesService {

    private final BackendFeignClient backendClient;
    private final AuthFeignClient authClient;

    public Map<String, Object> fetchMe() {
        try {
            UsuarioRecord record = authClient.getMe();
            if (record == null) return null;
            return Map.of(
                "id", record.id(),
                "email", record.email(),
                "rol", record.rol(),
                "username", record.username()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> fetchPerfilLegal(Integer userId) {
        try {
            PerfilLegalRecord record = backendClient.getPerfilLegalByUsuarioId(userId);
            if (record == null) return null;
            return Map.of(
                "usuarioId", record.usuarioId(),
                "nombre", record.nombre() != null ? record.nombre() : "",
                "apellido", record.apellido() != null ? record.apellido() : ""
            );
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> fetchMisSolicitudes() {
        try {
            return backendClient.getMisSolicitudes();
        } catch (Exception e) {
            return List.of();
        }
    }

    public Map<String, Object> fetchPreferencias(Integer userId) {
        try {
            return backendClient.getPreferenciasByUsuarioId(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public Long fetchPendingAdoptionsCount() {
        try {
            return backendClient.countSolicitudesPendientes();
        } catch (Exception e) {
            return 0L;
        }
    }

    public Long fetchPendingVolunteersCount() {
        try {
            return backendClient.countVoluntariosPendientes();
        } catch (Exception e) {
            return 0L;
        }
    }
}
