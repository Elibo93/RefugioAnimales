package es.refugio.auth.social_login.infrastructure;

import es.refugio.auth.domain.AuthCredentialEntity;
import es.refugio.auth.social_login.application.ProcessGoogleUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final ProcessGoogleUserUseCase processGoogleUserUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("El email no fue proporcionado por Google");
        }

        // Ejecutar lógica de persistencia
        AuthCredentialEntity user = processGoogleUserUseCase.execute(email, name);

        // Mapear rol de BD a GrantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRol().name());

        return new CustomOAuth2User(oauth2User, email, Collections.singletonList(authority));
    }
}
