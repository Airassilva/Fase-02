package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record LivroRankingResponse(
        @Schema(example = "4d3e2f1a-8b7c-6d5e-4f3a-2b1c0d9e8f7a") UUID livroId,
        @Schema(example = "Clean Architecture") String titulo,
        @Schema(example = "Robert C. Martin") String autor,
        @Schema(example = "9780134494166") String isbn,
        @Schema(example = "42") long total) {
}
