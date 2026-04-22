package es.refugio.auth.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.refugio.auth.domain.AuthCredentialEntity;
import es.refugio.auth.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthCredentialEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        java.util.List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRol().name()));
        
        if (user.getRol() == es.refugio.auth.domain.Rol.ROLE_VOLUNTARIO_ADOPTANTE) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VOLUNTARIO"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
