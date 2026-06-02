package es.refugio.refugio.domain.repository;

import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegalId;

public interface PerfilLegalRepository extends CRUDRepository<PerfilLegal, PerfilLegalId> {
    Optional<PerfilLegal> findByUsuarioId(Integer usuarioId);
    Optional<PerfilLegal> findByDni(String dni);
}
