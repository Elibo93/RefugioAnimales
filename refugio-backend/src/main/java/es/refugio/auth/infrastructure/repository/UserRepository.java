package es.refugio.auth.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.auth.domain.AuthCredentialEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AuthCredentialEntity, Integer> {
    Optional<AuthCredentialEntity> findByEmail(String email);
}
