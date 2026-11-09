package dev.aira.bibliotecanucleo.usuario.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID>,
        JpaSpecificationExecutor<UsuarioJpaEntity> {
    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select usuario from UsuarioJpaEntity usuario where usuario.id = :id")
    Optional<UsuarioJpaEntity> buscarPorIdComBloqueio(@Param("id") UUID id);
}
