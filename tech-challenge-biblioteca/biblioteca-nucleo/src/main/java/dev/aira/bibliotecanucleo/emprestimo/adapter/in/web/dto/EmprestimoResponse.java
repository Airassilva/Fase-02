package dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.dto;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;

import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoResponse(
        UUID id,
        UUID usuarioId,
        UUID livroId,
        String nomeLivro,
        String nomeUsuario,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        StatusEmprestimo status) {
}
