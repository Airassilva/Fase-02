package dev.aira.bibliotecanucleo.emprestimo.domain.model;

import dev.aira.bibliotecanucleo.emprestimo.domain.exception.EmprestimoJaDevolvidoException;

import java.util.UUID;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class Emprestimo {

    private UUID id;
    private final UUID usuarioId;
    private final UUID livroId;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private StatusEmprestimo status;

    private Emprestimo(UUID id, UUID usuarioId, UUID livroId, LocalDate dataEmprestimo,
                       LocalDate dataPrevistaDevolucao, LocalDate dataDevolucao,
                       StatusEmprestimo status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
    }

    public static Emprestimo registrar(UUID usuarioId, UUID livroId, LocalDate dataEmprestimo,
                                       Period prazo, PoliticaPrazo politica) {
        if (usuarioId == null || livroId == null || dataEmprestimo == null) {
            throw new IllegalArgumentException("Usuário, livro e data do empréstimo são obrigatórios");
        }
        if (prazo == null || prazo.isZero() || prazo.isNegative()) {
            throw new IllegalArgumentException("O prazo do empréstimo deve ser positivo");
        }
        return new Emprestimo(null, usuarioId, livroId, dataEmprestimo,
                politica.calcularDataPrevista(dataEmprestimo, prazo),
                null, StatusEmprestimo.ATIVO);
    }

    public static Emprestimo reconstituir(UUID id, UUID usuarioId, UUID livroId,
                                          LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao,
                                          LocalDate dataDevolucao, StatusEmprestimo status) {
        return new Emprestimo(id, usuarioId, livroId, dataEmprestimo,
                dataPrevistaDevolucao, dataDevolucao, status);
    }

    public void devolver(LocalDate dataDevolucao) {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            throw new EmprestimoJaDevolvidoException(id);
        }
        if (dataDevolucao == null || dataDevolucao.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("A data de devolução não pode ser anterior à do empréstimo");
        }
        this.dataDevolucao = dataDevolucao;
        this.status = StatusEmprestimo.DEVOLVIDO;
    }

    public boolean estaAtrasado(LocalDate hoje) {
        Objects.requireNonNull(hoje, "A data de referência é obrigatória");
        return dataDevolucao == null && hoje.isAfter(dataPrevistaDevolucao);
    }

    public boolean estaAtivo() {
        return status == StatusEmprestimo.ATIVO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getLivroId() {
        return livroId;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Emprestimo outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Emprestimo.class.hashCode();
    }
}
