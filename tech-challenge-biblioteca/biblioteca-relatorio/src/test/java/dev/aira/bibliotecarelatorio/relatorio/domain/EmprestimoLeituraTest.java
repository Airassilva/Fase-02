package dev.aira.bibliotecarelatorio.relatorio.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmprestimoLeituraTest {

    private final LocalDate emprestadoEm = LocalDate.of(2026, 11, 1);
    private final LocalDate previstoPara = LocalDate.of(2026, 11, 8);

    @Test
    void devolucaoRepetidaNaoSubstituiADataJaRegistrada() {
        EmprestimoLeitura emprestimo = emprestimo();
        LocalDate primeiraDevolucao = LocalDate.of(2026, 11, 10);
        emprestimo.registrarDevolucao(primeiraDevolucao);

        emprestimo.registrarDevolucao(LocalDate.of(2026, 11, 5));

        assertEquals(primeiraDevolucao, emprestimo.getDataDevolucao());
        assertTrue(emprestimo.estaDevolvido());
    }

    @Test
    void vencimentoNaoContaComoAtrasoMasDiaSeguinteConta() {
        EmprestimoLeitura emprestimo = emprestimo();

        assertFalse(emprestimo.estaAtrasado(previstoPara));
        assertTrue(emprestimo.estaAtrasado(previstoPara.plusDays(1)));
        emprestimo.registrarDevolucao(previstoPara.plusDays(2));
        assertFalse(emprestimo.estaAtrasado(previstoPara.plusDays(3)));
    }

    private EmprestimoLeitura emprestimo() {
        return EmprestimoLeitura.registrar(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Livro",
                "Autoria",
                "9781234567890",
                UUID.randomUUID(),
                emprestadoEm,
                previstoPara,
                null);
    }
}
