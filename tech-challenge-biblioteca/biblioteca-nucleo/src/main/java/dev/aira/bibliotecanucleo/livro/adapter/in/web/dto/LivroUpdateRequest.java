package dev.aira.bibliotecanucleo.livro.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroUpdateRequest(
        @Schema(example = "3")
        @NotNull(message = "A quantidade total é obrigatória")
        @Min(value = 1, message = "Deve haver ao menos um exemplar")
        Integer quantidadeTotal,

        @Schema(example = "Guia de arquitetura de software com foco em regras de dependência.")
        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 20, max = 500, message = "A descrição deve ter entre 20 e 500 caracteres")
        String descricao) {
}
