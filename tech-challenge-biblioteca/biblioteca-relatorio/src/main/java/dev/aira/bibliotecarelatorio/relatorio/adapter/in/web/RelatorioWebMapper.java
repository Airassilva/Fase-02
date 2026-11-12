package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web;

import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.EmprestimoAtivoResponse;
import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.EmprestimosAtivosResponse;
import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.LivroRankingResponse;
import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.RelatorioSemanalResponse;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimoAtivoConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageResult;
import dev.aira.bibliotecarelatorio.relatorio.application.query.RelatorioSemanalConsulta;
import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;

import java.util.List;

public class RelatorioWebMapper {

    private static final String FUSO_RELATORIO = "America/Recife";

    private RelatorioWebMapper() {
    }

    public static LivroRankingResponse paraResponse(LivroRanking livro) {
        return new LivroRankingResponse(
                livro.livroId(), livro.titulo(), livro.autor(), livro.isbn(), livro.totalEmprestimos());
    }

    public static RelatorioSemanalResponse paraResponse(RelatorioSemanalConsulta relatorio) {
        return new RelatorioSemanalResponse(
                new RelatorioSemanalResponse.Periodo(
                        relatorio.periodo().inicio(), relatorio.periodo().fim(), FUSO_RELATORIO),
                relatorio.geradoEm(),
                relatorio.totalEmprestimos(),
                relatorio.totalDevolucoes(),
                relatorio.emprestimosEmAberto(),
                relatorio.emprestimosAtrasados(),
                relatorio.porDia().stream()
                        .map(dia -> new RelatorioSemanalResponse.EmprestimosPorDia(dia.data(), dia.total()))
                        .toList(),
                relatorio.livrosMaisEmprestados().stream()
                        .map(livro -> new RelatorioSemanalResponse.LivroRanking(
                                livro.livroId(),
                                livro.titulo(),
                                livro.autor(),
                                livro.isbn(),
                                livro.totalEmprestimos()))
                        .toList());
    }

    public static EmprestimosAtivosResponse paraResponse(PageResult<EmprestimoAtivoConsulta> pagina) {
        List<EmprestimoAtivoResponse> content = pagina.content().stream()
                .map(emprestimo -> new EmprestimoAtivoResponse(
                        emprestimo.emprestimoId(),
                        emprestimo.livroId(),
                        emprestimo.livroTitulo(),
                        emprestimo.livroIsbn(),
                        emprestimo.dataEmprestimo(),
                        emprestimo.dataPrevistaDevolucao(),
                        emprestimo.atrasado(),
                        emprestimo.diasDeAtraso()))
                .toList();
        long totalPages = pagina.total() == 0 ? 0 : 1 + (pagina.total() - 1) / pagina.size();
        return new EmprestimosAtivosResponse(
                content,
                pagina.page(),
                pagina.size(),
                pagina.total(),
                Math.toIntExact(totalPages));
    }
}
