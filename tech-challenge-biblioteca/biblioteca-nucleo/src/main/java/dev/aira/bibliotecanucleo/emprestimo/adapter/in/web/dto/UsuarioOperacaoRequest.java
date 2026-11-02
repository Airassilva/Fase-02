package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UsuarioOperacaoRequest(@NotNull UUID usuarioId) {
}
