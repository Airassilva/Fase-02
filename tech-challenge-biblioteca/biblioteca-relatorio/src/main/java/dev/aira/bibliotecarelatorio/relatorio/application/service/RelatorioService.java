package dev.aira.bibliotecarelatorio.relatorio.application.service;

import dev.aira.bibliotecarelatorio.relatorio.application.port.in.ConsultarEmprestimosAtivosUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.ConsultarLivrosMaisEmprestadosUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.GerarRelatorioSemanalUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.GerarSnapshotsUseCase;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.ConsultaRelatorioPort;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.SnapshotRelatorioPort;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimoAtivoConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimosPorDiaConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageQuery;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageResult;
import dev.aira.bibliotecarelatorio.relatorio.application.query.RelatorioSemanalConsulta;
import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;
import dev.aira.bibliotecarelatorio.relatorio.domain.PeriodoSemanal;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class RelatorioService implements
        GerarRelatorioSemanalUseCase,
        ConsultarLivrosMaisEmprestadosUseCase,
        ConsultarEmprestimosAtivosUseCase,
        GerarSnapshotsUseCase {

    private static final ZoneId FUSO_RELATORIO = ZoneId.of("America/Recife");
    private static final String TIPO_SNAPSHOT_SEMANAL = "SEMANAL";

    private final ConsultaRelatorioPort consultas;
    private final SnapshotRelatorioPort snapshots;
    private final Clock clock;

    RelatorioService(ConsultaRelatorioPort consultas, SnapshotRelatorioPort snapshots, Clock clock) {
        this.consultas = consultas;
        this.snapshots = snapshots;
        this.clock = clock;
    }

    @Override
    public RelatorioSemanalConsulta gerar(LocalDate referencia) {
        LocalDate hoje = LocalDate.now(clock.withZone(FUSO_RELATORIO));
        PeriodoSemanal periodo = PeriodoSemanal.daSemanaDe(referencia == null ? hoje : referencia);
        LocalDate fimComDados;
        if (hoje.isBefore(periodo.inicio())) {
            fimComDados = periodo.inicio().minusDays(1);
        } else if (hoje.isBefore(periodo.fim())) {
            fimComDados = hoje;
        } else {
            fimComDados = periodo.fim();
        }

        Map<LocalDate, Long> totaisPorDia = consultas.contarEmprestimosPorDia(periodo.inicio(), fimComDados)
                .stream()
                .filter(dia -> !dia.data().isAfter(fimComDados))
                .collect(Collectors.toMap(EmprestimosPorDiaConsulta::data, EmprestimosPorDiaConsulta::total));

        List<EmprestimosPorDiaConsulta> porDia = periodo.dias()
                .map(data -> new EmprestimosPorDiaConsulta(data, totaisPorDia.getOrDefault(data, 0L)))
                .toList();
        long totalEmprestimos = porDia.stream().mapToLong(EmprestimosPorDiaConsulta::total).sum();

        return new RelatorioSemanalConsulta(
                periodo,
                clock.instant(),
                totalEmprestimos,
                consultas.contarDevolucoes(periodo.inicio(), fimComDados),
                consultas.contarEmprestimosEmAberto(),
                consultas.contarEmprestimosAtrasados(hoje),
                porDia,
                consultas.livrosMaisEmprestados(periodo.inicio(), fimComDados));
    }

    @Override
    public List<LivroRanking> consultar() {
        return consultas.livrosMaisEmprestados(20);
    }

    @Override
    public PageResult<EmprestimoAtivoConsulta> consultar(PageQuery pagina) {
        var resultado = consultas.consultarEmprestimosAtivos(pagina);
        LocalDate hoje = LocalDate.now(clock.withZone(FUSO_RELATORIO));
        List<EmprestimoAtivoConsulta> emprestimos = resultado.content().stream()
                .map(emprestimo -> {
                    boolean atrasado = emprestimo.dataPrevistaDevolucao().isBefore(hoje);
                    long diasDeAtraso = atrasado
                            ? ChronoUnit.DAYS.between(emprestimo.dataPrevistaDevolucao(), hoje)
                            : 0;
                    return new EmprestimoAtivoConsulta(
                            emprestimo.emprestimoId(),
                            emprestimo.livroId(),
                            emprestimo.livroTitulo(),
                            emprestimo.livroIsbn(),
                            emprestimo.dataEmprestimo(),
                            emprestimo.dataPrevistaDevolucao(),
                            atrasado,
                            diasDeAtraso);
                })
                .toList();
        return new PageResult<>(emprestimos, resultado.total(), resultado.page(), resultado.size());
    }

    @Override
    public void gerar() {
        LocalDate hoje = LocalDate.now(clock.withZone(FUSO_RELATORIO));
        PeriodoSemanal atual = PeriodoSemanal.daSemanaDe(hoje);
        snapshots.salvarOuAtualizar(TIPO_SNAPSHOT_SEMANAL, gerar(atual.inicio()));
        snapshots.salvarOuAtualizar(TIPO_SNAPSHOT_SEMANAL, gerar(atual.anterior().inicio()));
    }
}
