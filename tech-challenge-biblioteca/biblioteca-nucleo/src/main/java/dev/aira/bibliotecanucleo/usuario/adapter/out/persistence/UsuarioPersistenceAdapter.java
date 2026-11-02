package dev.aira.bibliotecanucleo.usuario.adapter.out.persistence;

import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.application.port.out.UsuarioRepositoryPort;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioPersistenceMapper usuarioPersistenceMapper;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository usuarioJpaRepository, UsuarioPersistenceMapper usuarioPersistenceMapper) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.usuarioPersistenceMapper = usuarioPersistenceMapper;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        var usuarioJpaEntity = usuarioPersistenceMapper.toEntity(usuario);
        var savedEntity = usuarioJpaRepository.save(usuarioJpaEntity);
        return usuarioPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        var usuarioJpaEntity = usuarioJpaRepository.findById(id);
        return usuarioJpaEntity.map(usuarioPersistenceMapper::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    @Override
    public PageResult<Usuario> listar(UsuarioFiltro filtro, PageQuery pageQuery) {
        var pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        var usuarioPage = usuarioJpaRepository.findAll(comFiltro(filtro), pageable);
        var usuarios = usuarioPage.getContent().stream()
                .map(usuarioPersistenceMapper::toDomain)
                .toList();
        return new PageResult<>(
                usuarios,
                usuarioPage.getTotalElements(),
                usuarioPage.getNumber(),
                usuarioPage.getSize()
        );
    }

    private Specification<UsuarioJpaEntity> comFiltro(UsuarioFiltro filtro) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (filtro.nome() != null && !filtro.nome().isBlank()) {
                String nome = "%" + escaparLike(filtro.nome().strip().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        nome,
                        '\\'
                ));
            }
            if (filtro.email() != null && !filtro.email().isBlank()) {
                String email = "%" + escaparLike(filtro.email().strip().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        email,
                        '\\'
                ));
            }
            if (filtro.tipo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), filtro.tipo()));
            }
            if (filtro.ativo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("ativo"), filtro.ativo()));
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
