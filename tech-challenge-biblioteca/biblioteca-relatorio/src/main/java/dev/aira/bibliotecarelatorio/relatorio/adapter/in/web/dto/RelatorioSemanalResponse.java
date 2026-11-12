package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RelatorioSemanalResponse(
        Periodo periodo,
        @Schema(example = "2026-11-12T03:30:00Z") Instant geradoEm,
        @Schema(example = "12") long totalEmprestimos,
        @Schema(example = "5") long totalDevolucoes,
        @Schema(example = "18") long emprestimosEmAberto,
        @Schema(example = "2") long emprestimosAtrasados,
        List<EmprestimosPorDia> porDia,
        List<LivroRanking> livrosMaisEmprestados) {

    public RelatorioSemanalResponse {
        porDia = List.copyOf(porDia);
        livrosMaisEmprestados = List.copyOf(livrosMaisEmprestados);
    }

    public record Periodo(
            @Schema(example = "2026-11-09") LocalDate inicio,
            @Schema(example = "2026-11-15") LocalDate fim,
            @Schema(example = "America/Recife") String fuso) {
    }

    public record EmprestimosPorDia(
            @Schema(example = "2026-11-09") LocalDate data,
            @Schema(example = "3") long total) {
    }

    public record LivroRanking(
            @Schema(example = "4d3e2f1a-8b7c-6d5e-4f3a-2b1c0d9e8f7a") UUID livroId,
            @Schema(example = "Clean Architecture") String titulo,
            @Schema(example = "Robert C. Martin") String autor,
            @Schema(example = "9780134494166") String isbn,
            @Schema(example = "7") long total) {
    }
}
