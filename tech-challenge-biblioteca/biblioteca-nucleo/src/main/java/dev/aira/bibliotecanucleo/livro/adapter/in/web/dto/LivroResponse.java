package dev.aira.bibliotecanucleo.livro.adapter.in.web.dto;

import java.util.UUID;

public record LivroResponse(
        UUID id,
        String titulo,
        String autor,
        String isbn,
        int quantidadeTotal,
        int quantidadeDisponivel,
        boolean disponivel
) {
}
