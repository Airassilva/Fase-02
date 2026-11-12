package dev.aira.bibliotecarelatorio.relatorio.application.service;

import dev.aira.bibliotecarelatorio.relatorio.application.port.in.EventoEmprestimoCommand;
import dev.aira.bibliotecarelatorio.relatorio.application.port.in.TipoEvento;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.EmprestimoLeituraRepositoryPort;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.EventoProcessadoPort;
import dev.aira.bibliotecarelatorio.relatorio.domain.EmprestimoLeitura;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessamentoEventoServiceTest {

    @Test
    void ignoraEventoDuplicado() {
        Memoria memoria = new Memoria();
        ProcessamentoEventoService service = new ProcessamentoEventoService(memoria, memoria);
        EventoEmprestimoCommand evento = evento(TipoEvento.EMPRESTIMO_REALIZADO, UUID.randomUUID(), null);

        service.processar(evento);
        service.processar(evento);

        assertEquals(1, memoria.salvamentos);
    }

    @Test
    void mantemDevolucaoQuandoEventoDeEmprestimoChegaDepois() {
        Memoria memoria = new Memoria();
        ProcessamentoEventoService service = new ProcessamentoEventoService(memoria, memoria);
        UUID emprestimoId = UUID.randomUUID();
        LocalDate devolvidoEm = LocalDate.of(2026, 11, 10);

        service.processar(evento(TipoEvento.LIVRO_DEVOLVIDO, emprestimoId, devolvidoEm));
        service.processar(evento(TipoEvento.EMPRESTIMO_REALIZADO, emprestimoId, null));

        assertTrue(memoria.emprestimos.get(emprestimoId).estaDevolvido());
        assertEquals(devolvidoEm, memoria.emprestimos.get(emprestimoId).getDataDevolucao());
        assertEquals(2, memoria.salvamentos);
    }

    @Test
    void reentregaDoEventoNaoAtualizaNovamenteOModelo() {
        Memoria memoria = new Memoria();
        ProcessamentoEventoService service = new ProcessamentoEventoService(memoria, memoria);
        EventoEmprestimoCommand devolucao = evento(
                TipoEvento.LIVRO_DEVOLVIDO,
                UUID.randomUUID(),
                LocalDate.of(2026, 11, 10));

        service.processar(devolucao);
        service.processar(devolucao);

        assertEquals(1, memoria.salvamentos);
    }

    private EventoEmprestimoCommand evento(TipoEvento tipo, UUID emprestimoId, LocalDate devolvidoEm) {
        return new EventoEmprestimoCommand(
                UUID.randomUUID(),
                tipo,
                Instant.parse("2026-11-10T12:00:00Z"),
                emprestimoId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Livro",
                "Autoria",
                "9781234567890",
                LocalDate.of(2026, 11, 8),
                LocalDate.of(2026, 11, 22),
                devolvidoEm);
    }

    private static class Memoria implements EventoProcessadoPort, EmprestimoLeituraRepositoryPort {
        private final Set<UUID> eventos = new HashSet<>();
        private final Map<UUID, EmprestimoLeitura> emprestimos = new HashMap<>();
        private int salvamentos;

        @Override
        public boolean registrarSeNovo(UUID eventId) {
            return eventos.add(eventId);
        }

        @Override
        public Optional<EmprestimoLeitura> buscarPorId(UUID emprestimoId) {
            return Optional.ofNullable(emprestimos.get(emprestimoId));
        }

        @Override
        public void salvar(EmprestimoLeitura emprestimo) {
            emprestimos.put(emprestimo.getEmprestimoId(), emprestimo);
            salvamentos++;
        }
    }
}
