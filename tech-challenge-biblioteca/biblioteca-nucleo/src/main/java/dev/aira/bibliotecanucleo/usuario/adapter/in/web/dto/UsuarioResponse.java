package dev.aira.bibliotecanucleo.usuario.adapter.in.web.dto;

import dev.aira.bibliotecanucleo.usuario.domain.model.TipoUsuario;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        TipoUsuario tipoUsuario) {
}
