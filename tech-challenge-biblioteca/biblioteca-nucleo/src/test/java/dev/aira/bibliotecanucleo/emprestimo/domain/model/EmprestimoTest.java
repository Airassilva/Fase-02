package dev.aira.bibliotecanucleo.emprestimo.domain.model;

import dev.aira.bibliotecanucleo.emprestimo.domain.exception.EmprestimoJaDevolvidoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmprestimoTest {

    @Test
    void calculaPrazoDevolucaoEUsaADataRecebidaParaAtraso() {
        UUID usuarioId = UUID.randomUUID();
        UUID livroId = UUID.randomUUID();
        LocalDate dataEmprestimo = LocalDate.of(2026, 11, 2);
        Emprestimo emprestimo = Emprestimo.registrar(
                usuarioId,
                livroId,
                dataEmprestimo,
                Period.ofDays(14),
                LocalDate::plus
        );

        assertEquals(dataEmprestimo.plusDays(14), emprestimo.getDataPrevistaDevolucao());
        assertFalse(emprestimo.estaAtrasado(dataEmprestimo.plusDays(14)));
        assertTrue(emprestimo.estaAtrasado(dataEmprestimo.plusDays(15)));
    }

    @Test
    void devolucaoRemoveEmprestimoDaCondicaoDeAtraso() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        Emprestimo emprestimo = Emprestimo.registrar(
                UUID.randomUUID(),
                UUID.randomUUID(),
                inicio,
                Period.ofDays(7),
                LocalDate::plus
        );

        emprestimo.devolver(inicio.plusDays(10));

        assertFalse(emprestimo.estaAtrasado(inicio.plusDays(20)));
        assertEquals(StatusEmprestimo.DEVOLVIDO, emprestimo.getStatus());
    }

    @Test
    void rejeitaSegundaDevolucao() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        Emprestimo emprestimo = Emprestimo.registrar(
                UUID.randomUUID(),
                UUID.randomUUID(),
                inicio,
                Period.ofDays(7),
                LocalDate::plus
        );
        emprestimo.devolver(inicio.plusDays(1));
        LocalDate segundaDataDevolucao = inicio.plusDays(2);

        assertThrows(EmprestimoJaDevolvidoException.class, () -> emprestimo.devolver(segundaDataDevolucao));
    }

    @Test
    void emprestimosPersistidosComMesmoIdSaoIguais() {
        UUID id = UUID.randomUUID();
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        Emprestimo primeiro = Emprestimo.reconstituir(
                id, UUID.randomUUID(), UUID.randomUUID(), inicio, inicio.plusDays(7), null, StatusEmprestimo.ATIVO);
        Emprestimo segundo = Emprestimo.reconstituir(
                id, primeiro.getUsuarioId(), primeiro.getLivroId(), inicio, inicio.plusDays(7), null, StatusEmprestimo.ATIVO);

        assertEquals(primeiro, segundo);
        assertEquals(primeiro.hashCode(), segundo.hashCode());
    }

    @Test
    void emprestimosSemIdSoSaoIguaisPorReferencia() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        Emprestimo primeiro = Emprestimo.registrar(
                UUID.randomUUID(), UUID.randomUUID(), inicio, Period.ofDays(7), LocalDate::plus);
        Emprestimo segundo = Emprestimo.registrar(
                UUID.randomUUID(), UUID.randomUUID(), inicio, Period.ofDays(7), LocalDate::plus);

        assertNotEquals(primeiro, segundo);
    }
}
