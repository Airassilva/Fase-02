package dev.aira.bibliotecarelatorio.relatorio.application.port.out;

import dev.aira.bibliotecarelatorio.relatorio.application.query.RelatorioSemanalConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.SnapshotGeradoConsulta;

import java.time.LocalDate;
import java.util.Optional;

public interface SnapshotRelatorioPort {
    void salvarOuAtualizar(String tipo, RelatorioSemanalConsulta relatorio);

    Optional<SnapshotGeradoConsulta> buscar(String tipo, LocalDate periodoInicio);
}
