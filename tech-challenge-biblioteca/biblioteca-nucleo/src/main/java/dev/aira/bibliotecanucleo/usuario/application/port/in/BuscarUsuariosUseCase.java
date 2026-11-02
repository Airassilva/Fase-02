package dev.aira.bibliotecanucleo.usuario.application.port.in;

import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import dev.aira.bibliotecanucleo.usuario.application.query.UsuarioFiltro;
import dev.aira.bibliotecanucleo.usuario.domain.model.Usuario;

import java.util.UUID;

public interface BuscarUsuariosUseCase {
    Usuario buscarPorId(UUID id);
    PageResult<Usuario> listar(UsuarioFiltro filtro, PageQuery pageQuery);
}
