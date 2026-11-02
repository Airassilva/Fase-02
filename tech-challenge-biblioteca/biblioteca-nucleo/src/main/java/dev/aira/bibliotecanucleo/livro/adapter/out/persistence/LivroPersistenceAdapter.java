package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import dev.aira.bibliotecanucleo.livro.application.exception.LivroNaoEncontradoException;
import dev.aira.bibliotecanucleo.livro.application.port.out.LivroRepositoryPort;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Locale;
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
    public PageResult<Livro> buscar(LivroFiltro filtro, PageQuery pageQuery) {
        var pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        var livroPage = livroJpaRepository.findAll(comFiltro(filtro), pageable);
        var livros = livroPage.getContent().stream()
                .map(livroPersistenceMapper::toDomain)
                .toList();
        return new PageResult<>(
                livros,
                livroPage.getTotalElements(),
                livroPage.getNumber(),
                livroPage.getSize()
        );
    }

    Specification<LivroJpaEntity> comFiltro(LivroFiltro filtro) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (filtro.nome() != null && !filtro.nome().isBlank()) {
                String nome = "%" + escaparLike(filtro.nome().strip().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), nome, '\\'));
            }
            if (filtro.autor() != null && !filtro.autor().isBlank()) {
                String autor = "%" + escaparLike(filtro.autor().strip().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("autor")), autor, '\\'));
            }
            if (filtro.isbn() != null && !filtro.isbn().isBlank()) {
                String isbn = filtro.isbn().replaceAll("[\\s-]", "").toLowerCase(Locale.ROOT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("isbn")),
                        "%" + escaparLike(isbn) + "%",
                        '\\'
                ));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private String escaparLike(String valor) {
        return valor.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
