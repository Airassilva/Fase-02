package dev.aira.bibliotecarelatorio.relatorio.application.query;

import java.util.List;

public record PageResult<T>(List<T> content, long total, int page, int size) {
    public PageResult {
        content = List.copyOf(content);
    }
}