package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

public record EmprestimoAtivoResponse(
        @Schema(example = "b2c1d2e3-4f5a-6789-abcd-ef0123456789") UUID emprestimoId,
        @Schema(example = "4d3e2f1a-8b7c-6d5e-4f3a-2b1c0d9e8f7a") UUID livroId,
        @Schema(example = "Clean Architecture") String livroTitulo,
        @Schema(example = "9780134494166") String livroIsbn,
        @Schema(example = "2026-11-08") LocalDate dataEmprestimo,
        @Schema(example = "2026-11-22") LocalDate dataPrevistaDevolucao,
        @Schema(example = "false") boolean atrasado,
        @Schema(example = "0") long diasDeAtraso) {
}
