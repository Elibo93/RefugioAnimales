package es.refugio.auth.social_login.infrastructure;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    
    private final OAuth2User oauth2User;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(OAuth2User oauth2User, String email, Collection<? extends GrantedAuthority> authorities) {
        this.oauth2User = oauth2User;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        // Retornamos el email. Así, JwtTokenProvider.generateToken() que busca por authentication.getName() 
        // encontrará correctamente al usuario por su email.
        return email;
    }

    public String getEmail() {
        return email;
    }
}
