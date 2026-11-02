package dev.aira.bibliotecanucleo.emprestimo.domain.model;

import java.time.LocalDate;
import java.time.Period;

public interface PoliticaPrazo {
    LocalDate calcularDataPrevista(LocalDate dataEmprestimo, Period prazo);
}
