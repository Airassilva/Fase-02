package dev.aira.bibliotecarelatorio.relatorio.adapter.in.web;

import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.EmprestimosAtivosResponse;
import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.LivroRankingResponse;
import dev.aira.bibliotecarelatorio.relatorio.adapter.in.web.dto.RelatorioSemanalResponse;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.ConsultarEmprestimosAtivosUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.ConsultarLivrosMaisEmprestadosUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.GerarRelatorioSemanalUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/relatorios")
@Tag(name = "Relatórios")
public class RelatorioController {

    private final ConsultarLivrosMaisEmprestadosUseCase consultarLivros;
    private final ConsultarEmprestimosAtivosUseCase consultarAtivos;
    private final GerarRelatorioSemanalUseCase gerarRelatorioSemanal;

    public RelatorioController(
            ConsultarLivrosMaisEmprestadosUseCase consultarLivros,
            ConsultarEmprestimosAtivosUseCase consultarAtivos,
            GerarRelatorioSemanalUseCase gerarRelatorioSemanal) {
        this.consultarLivros = consultarLivros;
        this.consultarAtivos = consultarAtivos;
        this.gerarRelatorioSemanal = gerarRelatorioSemanal;
    }

    @GetMapping("/livros-mais-emprestados")
    @Operation(summary = "Consultar os 20 livros mais emprestados")
    @ApiResponse(responseCode = "200", description = "Ranking geral dos livros mais emprestados")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    public List<LivroRankingResponse> livrosMaisEmprestados() {
        return consultarLivros.consultar().stream()
                .map(RelatorioWebMapper::paraResponse)
                .toList();
    }

    @GetMapping("/emprestimos-ativos")
    @Operation(summary = "Consultar empréstimos ativos")
    @ApiResponse(responseCode = "200", description = "Empréstimos ativos da página solicitada")
    @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    public EmprestimosAtivosResponse emprestimosAtivos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return RelatorioWebMapper.paraResponse(consultarAtivos.consultar(new PageQuery(page, size)));
    }

    @GetMapping("/semanal")
    @Operation(
            summary = "Consultar o relatório semanal",
            description = "A semana vai de segunda-feira a domingo no fuso America/Recife. "
                    + "Sem referência, considera a semana vigente; os totais refletem os eventos já "
                    + "consumidos e, portanto, têm consistência eventual.")
    @ApiResponse(responseCode = "200", description = "Relatório semanal")
    @ApiResponse(responseCode = "400", description = "A data de referência é inválida")
    public RelatorioSemanalResponse relatorioSemanal(
            @Parameter(description = "Data de referência no formato yyyy-MM-dd", example = "2026-11-12")
            @RequestParam(name = "referencia", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate referencia) {
        return RelatorioWebMapper.paraResponse(gerarRelatorioSemanal.gerar(referencia));
    }
}
