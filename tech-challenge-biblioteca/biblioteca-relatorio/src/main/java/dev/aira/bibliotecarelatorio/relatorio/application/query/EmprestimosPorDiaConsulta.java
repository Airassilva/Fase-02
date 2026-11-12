package dev.aira.bibliotecarelatorio.relatorio.application.query;

import java.time.LocalDate;

public record EmprestimosPorDiaConsulta(LocalDate data, long total) {
}
