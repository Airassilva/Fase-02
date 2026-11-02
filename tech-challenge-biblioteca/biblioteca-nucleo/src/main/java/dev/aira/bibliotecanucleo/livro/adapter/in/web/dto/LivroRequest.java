package dev.aira.bibliotecanucleo.livro.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroRequest(

        @Schema(example = "Clean Architecture")
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
        String titulo,

        @Schema(example = "Robert C. Martin")
        @NotBlank(message = "O autor é obrigatório")
        @Size(max = 150, message = "O autor deve ter no máximo 150 caracteres")
        String autor,

        @Schema(example = "978-0-13-449416-6", description = "ISBN-10 ou ISBN-13, com ou sem hífens")
        @NotBlank(message = "O ISBN é obrigatório")
        @Size(max = 17, message = "O ISBN deve ter no máximo 17 caracteres")
        String isbn,

        @Schema(example = "3")
        @NotNull(message = "A quantidade total é obrigatória")
        @Min(value = 1, message = "Deve haver ao menos um exemplar")
        Integer quantidadeTotal,

        @Schema(example = "Guia de arquitetura de software com foco em regras de dependência.")
        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 20, max = 500, message = "A descrição deve ter entre 20 e 500 caracteres")
        String descricao) {
}
