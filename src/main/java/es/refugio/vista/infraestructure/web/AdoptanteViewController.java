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
import es.refugio.refugio.application.command.usuario.EditUsuarioCommand;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
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
    private final FindAnimalService findAnimalService;
    private final FindUsuarioService findUsuarioService;
    private final EditUsuarioService editUsuarioService;

    @GetMapping(WebRoutes.ADOPTANTES_MODAL_CONVERTIR)
    public String modalConvertir(@RequestParam Integer animalId, Model model, Authentication authentication) {
        String email = authentication.getName();
        AuthCredentialEntity userCredential = userRepository.findByEmail(email).orElse(null);
        
        if (userCredential != null) {
            Usuario usuario = findUsuarioService.findById(new UsuarioId(userCredential.getId()));
            model.addAttribute("usuario", usuario);
        }

        Animal animal = findAnimalService.findById(new AnimalId(animalId));
        model.addAttribute("animal", animal);

        return "fragments/modals/modal-conversion-directa :: modal";
    }

    @PostMapping(WebRoutes.ADOPTANTES_CONVERTIR_Y_SOLICITAR)
    public String convertirYSolicitar(@RequestBody ConvertirAdoptanteRequest request, Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            AuthCredentialEntity userCredential = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 1. Actualizar Datos del Usuario y Rol
            Usuario usuarioActual = findUsuarioService.findById(new UsuarioId(userCredential.getId()));
            
            // Actualizamos nombre y apellido si han cambiado
            editUsuarioService.update(new EditUsuarioCommand(
                    usuarioActual.getId(),
                    request.getNombre(),
                    request.getApellido(),
                    usuarioActual.getEmail(),
                    usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "",
                    Rol.ROLE_ADOPTANTE
            ));

            // 2. Crear Perfil de Adoptante
            Adoptante adoptante = createAdoptanteService.createAdoptante(new CreateAdoptanteCommand(
                    userCredential.getId(),
                    request.getDni(),
                    request.getDireccion(),
                    request.getFechaNacimiento()));

            // 3. Crear Solicitud de Adopción
            String comentario = request.getComentario() != null && !request.getComentario().isBlank() 
                    ? request.getComentario() 
                    : "Solicitud registrada tras completar el perfil de adoptante.";

            solicitudService.create(new CreateSolicitudAdopcionCommand(
                    request.getAnimalId(),
                    adoptante.getId().getValue(),
                    LocalDateTime.now(),
                    comentario));

            return "fragments/content/solicitud-creada :: success-modal";
        } catch (Exception e) {
            // En caso de error, volvemos a cargar los datos necesarios para el formulario
            try {
                Animal animal = findAnimalService.findById(new AnimalId(request.getAnimalId()));
                model.addAttribute("animal", animal);
                
                // Re-enviamos el usuario con los datos que intentó poner (o los originales)
                model.addAttribute("usuario", Usuario.builder()
                        .nombre(request.getNombre())
                        .apellido(request.getApellido())
                        .build());
                
                model.addAttribute("errorMessage", "Error al procesar la solicitud: " + e.getMessage());
            } catch (Exception fatal) {
                model.addAttribute("errorMessage", "Error crítico al recuperar datos: " + fatal.getMessage());
            }
            return "fragments/modals/modal-conversion-directa :: modal";
        }
    }
}
