package dev.aira.bibliotecarelatorio.relatorio.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.previousOrSame;

public record PeriodoSemanal(LocalDate inicio, LocalDate fim) {

    public PeriodoSemanal {
        Objects.requireNonNull(inicio, "O início do período é obrigatório");
        Objects.requireNonNull(fim, "O fim do período é obrigatório");
        if (!inicio.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            throw new IllegalArgumentException("A semana deve começar em uma segunda-feira");
        }
        if (!fim.equals(inicio.plusDays(6))) {
            throw new IllegalArgumentException("A semana deve terminar no domingo seguinte");
        }
    }

    public static PeriodoSemanal daSemanaDe(LocalDate data) {
        Objects.requireNonNull(data, "A data de referência é obrigatória");
        LocalDate segunda = data.with(previousOrSame(DayOfWeek.MONDAY));
        return new PeriodoSemanal(segunda, segunda.plusDays(6));
    }

    public PeriodoSemanal anterior() {
        return new PeriodoSemanal(inicio.minusWeeks(1), fim.minusWeeks(1));
    }

    public boolean contem(LocalDate data) {
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    // Os 7 dias da semana, em ordem
    public Stream<LocalDate> dias() {
        return inicio.datesUntil(fim.plusDays(1));
    }
}