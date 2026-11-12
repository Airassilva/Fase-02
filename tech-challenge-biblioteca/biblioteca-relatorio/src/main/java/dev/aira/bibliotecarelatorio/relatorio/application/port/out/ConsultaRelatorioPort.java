package dev.aira.bibliotecarelatorio.relatorio.application.port.out;

import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimoAtivoConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimosPorDiaConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageQuery;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageResult;
import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;

import java.time.LocalDate;
import java.util.List;

public interface ConsultaRelatorioPort {
    List<LivroRanking> livrosMaisEmprestados(int limite);

    List<LivroRanking> livrosMaisEmprestados(LocalDate inicio, LocalDate fim);

    long contarDevolucoes(LocalDate inicio, LocalDate fim);

    long contarEmprestimosEmAberto();

    long contarEmprestimosAtrasados(LocalDate hoje);

    List<EmprestimosPorDiaConsulta> contarEmprestimosPorDia(LocalDate inicio, LocalDate fim);

    PageResult<EmprestimoAtivoConsulta> consultarEmprestimosAtivos(PageQuery pagina);
}
