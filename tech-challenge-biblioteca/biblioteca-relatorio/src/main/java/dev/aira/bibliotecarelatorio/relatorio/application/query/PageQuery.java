package dev.aira.bibliotecarelatorio.relatorio.application.query;

public record PageQuery(int page, int size) {
    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException("A página não pode ser negativa");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("O tamanho da página deve estar entre 1 e 100");
        }
    }
}