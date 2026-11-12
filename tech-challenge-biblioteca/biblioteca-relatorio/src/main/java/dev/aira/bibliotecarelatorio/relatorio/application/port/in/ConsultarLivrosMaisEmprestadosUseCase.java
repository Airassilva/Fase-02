package dev.aira.bibliotecarelatorio.relatorio.application.port.in;

import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;

import java.util.List;

public interface ConsultarLivrosMaisEmprestadosUseCase {
    List<LivroRanking> consultar();
}
