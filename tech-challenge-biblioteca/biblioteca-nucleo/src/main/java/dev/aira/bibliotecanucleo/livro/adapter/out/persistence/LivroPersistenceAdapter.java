package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import dev.aira.bibliotecanucleo.livro.application.exception.LivroNaoEncontradoException;
import dev.aira.bibliotecanucleo.livro.application.port.out.LivroRepositoryPort;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class LivroPersistenceAdapter implements LivroRepositoryPort {
    private final LivroJpaRepository livroJpaRepository;
    private final LivroPersistenceMapper livroPersistenceMapper;

    public LivroPersistenceAdapter(LivroJpaRepository livroJpaRepository, LivroPersistenceMapper livroPersistenceMapper) {
        this.livroJpaRepository = livroJpaRepository;
        this.livroPersistenceMapper = livroPersistenceMapper;
    }

    @Override
    public boolean existePorIsbn(Isbn isbn) {
       return livroJpaRepository.existsByIsbn(isbn.valor());
    }

    @Override
    public Livro salvar(Livro livro) {
        LivroJpaEntity entity = livroPersistenceMapper.toEntity(livro);
        LivroJpaEntity savedEntity = livroJpaRepository.save(entity);
        return livroPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Livro buscarPorId(UUID id) {
        LivroJpaEntity entity = livroJpaRepository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException(id));
        return livroPersistenceMapper.toDomain(entity);
    }

    @Override
    public void excluir(UUID id) {
        if (!livroJpaRepository.existsById(id)) {
            throw new LivroNaoEncontradoException(id);
        }
        livroJpaRepository.deleteById(id);
    }

    @Override
    public List<Livro> buscarTodos() {
        List<LivroJpaEntity> entities = livroJpaRepository.findAll();
        return entities.stream()
                .map(livroPersistenceMapper::toDomain)
                .toList();
    }
}
