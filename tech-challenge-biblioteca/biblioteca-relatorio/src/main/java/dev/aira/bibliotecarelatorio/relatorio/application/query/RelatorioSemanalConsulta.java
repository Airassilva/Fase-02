package dev.aira.bibliotecarelatorio.relatorio.application.query;

import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;
import dev.aira.bibliotecarelatorio.relatorio.domain.PeriodoSemanal;

import java.time.Instant;
import java.util.List;

public record RelatorioSemanalConsulta(
        PeriodoSemanal periodo,
        Instant geradoEm,
        long totalEmprestimos,
        long totalDevolucoes,
        long emprestimosEmAberto,
        long emprestimosAtrasados,
        List<EmprestimosPorDiaConsulta> porDia,
        List<LivroRanking> livrosMaisEmprestados) {

    public RelatorioSemanalConsulta {
        porDia = List.copyOf(porDia);
        livrosMaisEmprestados = List.copyOf(livrosMaisEmprestados);
    }
}
