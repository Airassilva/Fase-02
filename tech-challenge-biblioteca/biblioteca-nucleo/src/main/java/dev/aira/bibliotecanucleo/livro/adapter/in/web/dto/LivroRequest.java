package dev.aira.bibliotecanucleo.livro.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroRequest(
        @NotBlank @Size(max = 255) String titulo,
        @NotBlank @Size(max = 255) String autor,
        @NotBlank String isbn,
        @NotNull @Min(1) Integer quantidadeTotal,
        @Size(min = 20, max = 500) String descricao
) {
}
