package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto;

import java.util.UUID;

public record EmprestimoUpdateRequest(
        UUID usuarioId,
        UUID livroId) {
}
