package dev.aira.bibliotecanucleo.emprestimo.application.model;

import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoAtrasado(
        UUID id,
        UUID usuarioId,
        UUID livroId,
        LocalDate dataPrevistaDevolucao
) {
}
