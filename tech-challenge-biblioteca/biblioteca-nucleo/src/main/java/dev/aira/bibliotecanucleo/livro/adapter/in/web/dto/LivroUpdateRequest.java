package dev.aira.bibliotecanucleo.livro.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LivroUpdateRequest(
        @NotNull @Min(1) Integer quantidadeTotal,
        @Size(min = 20, max = 500) String descricao) {
}
