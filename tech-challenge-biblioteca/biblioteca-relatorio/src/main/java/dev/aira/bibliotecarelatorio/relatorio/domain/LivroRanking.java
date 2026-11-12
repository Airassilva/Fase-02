package dev.aira.bibliotecarelatorio.relatorio.domain;

import java.util.UUID;

public record LivroRanking(
        UUID livroId,
        String titulo,
        String autor,
        String isbn,
        long totalEmprestimos) {

    public LivroRanking {
        if (totalEmprestimos < 0) {
            throw new IllegalArgumentException("O total de empréstimos não pode ser negativo");
        }
    }
}
