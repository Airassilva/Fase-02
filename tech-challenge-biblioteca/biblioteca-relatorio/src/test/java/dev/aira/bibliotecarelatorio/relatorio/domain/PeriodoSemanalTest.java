package dev.aira.bibliotecarelatorio.relatorio.domain;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PeriodoSemanalTest {

    @Test
    void encontraMesmaSemanaParaCadaDiaEParaViradaDoAno() {
        LocalDate segunda = LocalDate.of(2026, 12, 28);
        IntStream.range(0, 7).forEach(dia -> {
            PeriodoSemanal periodo = PeriodoSemanal.daSemanaDe(segunda.plusDays(dia));
            assertEquals(segunda, periodo.inicio());
            assertEquals(LocalDate.of(2027, 1, 3), periodo.fim());
        });
    }

    @Test
    void domingoANoiteESegundaCedoPertencemASuasSemanasCorretas() {
        PeriodoSemanal domingo = PeriodoSemanal.daSemanaDe(LocalDate.of(2026, 11, 8));
        PeriodoSemanal segunda = PeriodoSemanal.daSemanaDe(LocalDate.of(2026, 11, 9));

        assertEquals(LocalDate.of(2026, 11, 2), domingo.inicio());
        assertEquals(LocalDate.of(2026, 11, 8), domingo.fim());
        assertEquals(LocalDate.of(2026, 11, 9), segunda.inicio());
        assertEquals(7, segunda.dias().count());
    }

    @Test
    void rejeitaPeriodoQueNaoVaiDeSegundaADomingo() {
        LocalDate domingo = LocalDate.of(2026, 11, 8);
        LocalDate sabadoSeguinte = LocalDate.of(2026, 11, 14);
        LocalDate segunda = LocalDate.of(2026, 11, 2);
        LocalDate sabado = LocalDate.of(2026, 11, 7);

        assertThrows(IllegalArgumentException.class, () -> new PeriodoSemanal(domingo, sabadoSeguinte));
        assertThrows(IllegalArgumentException.class, () -> new PeriodoSemanal(segunda, sabado));
        assertEquals(DayOfWeek.MONDAY, PeriodoSemanal.daSemanaDe(
                LocalDate.of(2026, 11, 8)).inicio().getDayOfWeek());
    }
}
