package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LivroJpaRepository extends JpaRepository<LivroJpaEntity, UUID> {
}
