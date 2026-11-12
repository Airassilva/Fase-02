package dev.aira.bibliotecarelatorio.relatorio.application.query;

import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoAtivoConsulta(
        UUID emprestimoId,
        UUID livroId,
        String livroTitulo,
        String livroIsbn,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        boolean atrasado,
        long diasDeAtraso) {
}
