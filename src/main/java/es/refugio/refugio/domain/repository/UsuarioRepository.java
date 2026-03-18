package es.refugio.refugio.domain.repository;

import java.util.Optional;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

public interface UsuarioRepository extends CRUDRepository<Usuario, UsuarioId> {

    public Optional<Usuario> getByName(String name);

}
