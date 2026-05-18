package es.refugio.auth.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.refugio.auth.domain.AuthCredentialEntity;
import es.refugio.auth.domain.Rol;
import es.refugio.auth.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthCredentialEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRol().name()));
        
        if (user.getRol() == Rol.ROLE_VOLUNTARIO_ADOPTANTE) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VOLUNTARIO"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ADOPTANTE"));
        }

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getId()
        );
    }
}
