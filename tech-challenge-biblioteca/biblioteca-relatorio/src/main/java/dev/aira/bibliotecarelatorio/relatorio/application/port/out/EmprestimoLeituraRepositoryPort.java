package dev.aira.bibliotecarelatorio.relatorio.application.port.out;

import dev.aira.bibliotecarelatorio.relatorio.domain.EmprestimoLeitura;

import java.util.Optional;
import java.util.UUID;

public interface EmprestimoLeituraRepositoryPort {
    Optional<EmprestimoLeitura> buscarPorId(UUID emprestimoId);

    void salvar(EmprestimoLeitura emprestimo);
}
