package dev.aira.bibliotecarelatorio.relatorio.application.port.in;

import dev.aira.bibliotecarelatorio.relatorio.application.query.RelatorioSemanalConsulta;

import java.time.LocalDate;

public interface GerarRelatorioSemanalUseCase {
    RelatorioSemanalConsulta gerar(LocalDate referencia);
}
