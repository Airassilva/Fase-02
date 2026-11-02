package dev.aira.bibliotecanucleo.usuario.domain.model;

import java.time.Period;

public enum TipoUsuario {
    ALUNO(Period.ofDays(14), 3),
    MEMBRO(Period.ofDays(7), 5);

    private final Period prazoEmprestimo;
    private final int limiteEmprestimosAtivos;

    TipoUsuario(Period prazoEmprestimo, int limiteEmprestimosAtivos) {
        this.prazoEmprestimo = prazoEmprestimo;
        this.limiteEmprestimosAtivos = limiteEmprestimosAtivos;
    }

    public Period prazoEmprestimo() { return prazoEmprestimo; }
    public int limiteEmprestimosAtivos() { return limiteEmprestimosAtivos; }
}
