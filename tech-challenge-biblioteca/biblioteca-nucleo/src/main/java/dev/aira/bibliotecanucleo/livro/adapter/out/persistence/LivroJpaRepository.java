package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LivroJpaRepository extends JpaRepository<LivroJpaEntity, UUID>,
        JpaSpecificationExecutor<LivroJpaEntity> {
    boolean existsByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select livro from LivroJpaEntity livro where livro.id = :id")
    Optional<LivroJpaEntity> buscarPorIdComBloqueio(UUID id);
}
