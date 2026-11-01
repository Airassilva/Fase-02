package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import dev.aira.bibliotecanucleo.livro.domain.port.out.LivroRepositoryPort;
import org.springframework.stereotype.Repository;

@Repository
public class LivroPersistenceAdapter implements LivroRepositoryPort {
    private final LivroJpaRepository livroJpaRepository;
    private final LivroPersistenceMapper livroPersistenceMapper;

    public LivroPersistenceAdapter(LivroJpaRepository livroJpaRepository, LivroPersistenceMapper livroPersistenceMapper) {
        this.livroJpaRepository = livroJpaRepository;
        this.livroPersistenceMapper = livroPersistenceMapper;
    }
}
