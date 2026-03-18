package es.refugio.refugio.application.usecase.usuario;

import java.time.LocalDateTime;

import es.refugio.refugio.application.command.usuario.CreateUsuarioCommand;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateUsuarioUseCase {
    private final UsuarioRepository personaRepository;

    public Usuario create(CreateUsuarioCommand comando) {
        Usuario persona = Usuario.builder()
                .dni(comando.dni())
                .nombre(comando.nombre())
                .apellido(comando.apellido())
                .email(comando.email())
                .telefono(comando.telefono())
                .direccion(comando.direccion())
                .fechaNacimiento(comando.fechaNacimiento())
                .createdAt(LocalDateTime.now())
                .build();
        return personaRepository.save(persona);

    }

}
