package dev.aira.bibliotecanucleo.usuario.application.query;

import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;

public record UsuarioFiltro(String nome, String email, TipoUsuario tipo, Boolean ativo) {
}
