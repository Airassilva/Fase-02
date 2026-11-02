package dev.aira.bibliotecanucleo.usuario.adapter.out.persistence;

import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceMapper {

    public UsuarioJpaEntity toEntity(Usuario usuario) {
        return new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo(),
                usuario.isAtivo(),
                usuario.getCriadoEm()
        );
    }

    public Usuario toDomain(UsuarioJpaEntity entity) {
        return Usuario.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTipo(),
                entity.isAtivo(),
                entity.getCriadoEm()
        );
    }
}
