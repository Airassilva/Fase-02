package dev.aira.bibliotecarelatorio.relatorio.application.service;

import dev.aira.bibliotecarelatorio.relatorio.application.port.out.ConsultaRelatorioPort;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.SnapshotRelatorioPort;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimoAtivoConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimosPorDiaConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageQuery;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageResult;
import dev.aira.bibliotecarelatorio.relatorio.application.query.RelatorioSemanalConsulta;
import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelatorioServiceTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-11-11T12:00:00Z"),
            ZoneId.of("UTC"));

    @Test
    void completaOsSeteDiasECalculaTotalPelosDadosAgregados() {
        ConsultaFake consulta = new ConsultaFake();
        consulta.porDia = List.of(
                new EmprestimosPorDiaConsulta(LocalDate.of(2026, 11, 9), 2),
                new EmprestimosPorDiaConsulta(LocalDate.of(2026, 11, 12), 4));
        RelatorioService service = new RelatorioService(consulta, new SnapshotFake(), CLOCK);

        RelatorioSemanalConsulta relatorio = service.gerar(null);

        assertEquals(LocalDate.of(2026, 11, 9), relatorio.periodo().inicio());
        assertEquals(7, relatorio.porDia().size());
        assertEquals(List.of(2L, 0L, 0L, 0L, 0L, 0L, 0L),
                relatorio.porDia().stream().map(EmprestimosPorDiaConsulta::total).toList());
        assertEquals(2, relatorio.totalEmprestimos());
        assertEquals(1, relatorio.emprestimosAtrasados());
    }

    @Test
    void semanaVigenteUsaODiaLocalDeRecife() {
        Clock pertoDaMeiaNoiteUtc = Clock.fixed(
                Instant.parse("2026-11-09T01:00:00Z"),
                ZoneId.of("UTC"));
        RelatorioService service = new RelatorioService(new ConsultaFake(), new SnapshotFake(), pertoDaMeiaNoiteUtc);

        RelatorioSemanalConsulta relatorio = service.gerar(null);

        assertEquals(LocalDate.of(2026, 11, 2), relatorio.periodo().inicio());
    }

    @Test
    void geraSnapshotsDaSemanaAtualEAnterior() {
        SnapshotFake snapshots = new SnapshotFake();
        RelatorioService service = new RelatorioService(new ConsultaFake(), snapshots, CLOCK);

        service.gerar();

        assertEquals(
                List.of(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 2)),
                snapshots.relatorios.stream().map(r -> r.periodo().inicio()).toList());
    }

    @Test
    void marcaEmprestimoAtivoAtrasadoECalculaDias() {
        ConsultaFake consulta = new ConsultaFake();
        consulta.ativos = List.of(new EmprestimoAtivoConsulta(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Livro",
                "9781234567890",
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 8),
                false,
                0));
        RelatorioService service = new RelatorioService(consulta, new SnapshotFake(), CLOCK);

        EmprestimoAtivoConsulta ativo = service.consultar(new PageQuery(0, 10)).content().getFirst();

        assertTrue(ativo.atrasado());
        assertEquals(3, ativo.diasDeAtraso());
    }

    private static class ConsultaFake implements ConsultaRelatorioPort {
        private List<EmprestimosPorDiaConsulta> porDia = List.of();
        private List<EmprestimoAtivoConsulta> ativos = List.of();

        @Override
        public List<LivroRanking> livrosMaisEmprestados(int limite) {
            return List.of();
        }

        @Override
        public List<LivroRanking> livrosMaisEmprestados(LocalDate inicio, LocalDate fim) {
            return List.of();
        }

        @Override
        public long contarDevolucoes(LocalDate inicio, LocalDate fim) {
            return 0;
        }

        @Override
        public long contarEmprestimosEmAberto() {
            return 2;
        }

        @Override
        public long contarEmprestimosAtrasados(LocalDate hoje) {
            return 1;
        }

        @Override
        public List<EmprestimosPorDiaConsulta> contarEmprestimosPorDia(LocalDate inicio, LocalDate fim) {
            return porDia;
        }

        @Override
        public PageResult<EmprestimoAtivoConsulta> consultarEmprestimosAtivos(PageQuery pagina) {
            return new PageResult<>(ativos, ativos.size(), pagina.page(), pagina.size());
        }
    }

    private static class SnapshotFake implements SnapshotRelatorioPort {
        private final List<RelatorioSemanalConsulta> relatorios = new ArrayList<>();

        @Override
        public void salvarOuAtualizar(String tipo, RelatorioSemanalConsulta relatorio) {
            relatorios.add(relatorio);
        }

        @Override
        public Optional<dev.aira.bibliotecarelatorio.relatorio.application.query.SnapshotGeradoConsulta> buscar(
                String tipo,
                LocalDate periodoInicio) {
            return Optional.empty();
        }
    }
}
