package es.refugio.vista.infraestructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import es.refugio.auth.domain.AuthCredentialEntity;
import es.refugio.auth.domain.Rol;
import es.refugio.auth.infrastructure.repository.UserRepository;
import es.refugio.refugio.application.command.adoptante.CreateAdoptanteCommand;
import es.refugio.refugio.application.command.solicitud_adopcion.CreateSolicitudAdopcionCommand;
import es.refugio.refugio.application.service.adoptante.CreateAdoptanteService;
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.infraestructure.web.dto.adoptante.ConvertirAdoptanteRequest;
import es.refugio.vista.infraestructure.web.constants.WebRoutes;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdoptanteViewController {

    private final UserRepository userRepository;
    private final CreateAdoptanteService createAdoptanteService;
    private final CreateSolicitudAdopcionService solicitudService;

    @GetMapping(WebRoutes.adoptantes_MODAL_CONVERTIR)
    public String modalConvertir(@RequestParam Integer animalId, Model model, Authentication authentication) {
        String email = authentication.getName();
        AuthCredentialEntity user = userRepository.findByEmail(email).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("animalId", animalId);

        return "fragments/modals/modal-conversion-directa :: modal";
    }

    @PostMapping(WebRoutes.adoptantes_CONVERTIR_Y_SOLICITAR)
    public String convertirYSolicitar(@RequestBody ConvertirAdoptanteRequest request, Authentication authentication) {
        String email = authentication.getName();
        AuthCredentialEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Actualizar Rol
        user.setRol(Rol.ROLE_ADOPTANTE);
        userRepository.save(user);

        // 2. Crear Perfil de Adoptante
        Adoptante adoptante = createAdoptanteService.createAdoptante(new CreateAdoptanteCommand(
                user.getId(),
                request.getDni(),
                request.getDireccion(),
                request.getFechaNacimiento()));

        // 3. Crear Solicitud de Adopción
        solicitudService.create(new CreateSolicitudAdopcionCommand(
                request.getAnimalId(),
                adoptante.getId().getValue(),
                LocalDateTime.now(),
                "Solicitud automática tras conversión de perfil."));

        return "fragments/content/solicitud-creada :: success-modal";
    }
}
