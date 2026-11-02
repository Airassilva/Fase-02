package dev.aira.bibliotecanucleo.usuario.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID>,
        JpaSpecificationExecutor<UsuarioJpaEntity> {
    boolean existsByEmail(String email);
}
