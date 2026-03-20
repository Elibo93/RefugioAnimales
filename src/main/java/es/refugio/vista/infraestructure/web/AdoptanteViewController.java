package es.refugio.vista.infraestructure.web;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.security.core.Authentication;

    import es.refugio.auth.domain.AuthCredentialEntity;
    import es.refugio.auth.infrastructure.repository.UserRepository;
    import es.refugio.vista.infraestructure.web.constants.WebRoutes;
    import lombok.RequiredArgsConstructor;

    @Controller
    @RequiredArgsConstructor
    public class AdoptanteViewController {
        
        private final UserRepository userRepository;

        @GetMapping(WebRoutes.adoptantes_MODAL_CONVERTIR)
        public String modalConvertir(@RequestParam Integer animalId, Model model, Authentication authentication) {
            String email = authentication.getName();
            AuthCredentialEntity user = userRepository.findByEmail(email).orElse(null);
            
            model.addAttribute("user", user);
            model.addAttribute("animalId", animalId);
            
            return "fragments/modals/modal-conversion-directa :: modal";
        }
    }
