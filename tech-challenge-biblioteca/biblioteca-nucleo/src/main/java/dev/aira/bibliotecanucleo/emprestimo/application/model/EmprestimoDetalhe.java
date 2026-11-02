package dev.aira.bibliotecanucleo.emprestimo.application.model;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;

import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoDetalhe(
        UUID id,
        UUID usuarioId,
        UUID livroId,
        String nomeLivro,
        String nomeUsuario,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        StatusEmprestimo status
) {}
