package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record EmprestimosAtivosResponse(
        @Schema(description = "Empréstimos da página atual") List<EmprestimoAtivoResponse> content,
        @Schema(example = "0") int page,
        @Schema(example = "20") int size,
        @Schema(example = "56") long totalElements,
        @Schema(example = "3") int totalPages) {

    public EmprestimosAtivosResponse {
        content = List.copyOf(content);
    }
}
