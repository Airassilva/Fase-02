package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EmprestimoRequest(
        @NotNull UUID usuarioId,
        @NotNull UUID livroId
) {
}
