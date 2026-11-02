package dev.aira.bibliotecanucleo.usuario.application.port.out;

import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(UUID id);
    boolean existePorEmail(String email);
    PageResult<Usuario> listar(UsuarioFiltro filtro, PageQuery pageQuery);
}
