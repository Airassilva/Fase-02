package dev.aira.bibliotecarelatorio.relatorio.domain;

import java.time.LocalDate;
import java.util.UUID;

public class EmprestimoLeitura {
    private final UUID emprestimoId;
    private final UUID livroId;
    private final String livroTitulo;
    private final String livroAutor;
    private final String livroIsbn;
    private final UUID usuarioId;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;

    private EmprestimoLeitura(UUID emprestimoId, UUID livroId, String livroTitulo,
                              String livroAutor, String livroIsbn, UUID usuarioId,
                              LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao,
                              LocalDate dataDevolucao) {
        this.emprestimoId = emprestimoId;
        this.livroId = livroId;
        this.livroTitulo = livroTitulo;
        this.livroAutor = livroAutor;
        this.livroIsbn = livroIsbn;
        this.usuarioId = usuarioId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
    }

    public static EmprestimoLeitura registrar(UUID emprestimoId, UUID livroId, String livroTitulo,
                                              String livroAutor, String livroIsbn, UUID usuarioId,
                                              LocalDate dataEmprestimo,
                                              LocalDate dataPrevistaDevolucao,
                                              LocalDate dataDevolucao) {
        if (emprestimoId == null || livroId == null || usuarioId == null) {
            throw new IllegalArgumentException("Os ids do empréstimo, do livro e do usuário são obrigatórios");
        }
        if (livroTitulo == null || livroTitulo.isBlank()) {
            throw new IllegalArgumentException("O título do livro é obrigatório");
        }
        if (livroAutor == null || livroAutor.isBlank()) {
            throw new IllegalArgumentException("O autor do livro é obrigatório");
        }
        if (livroIsbn == null || livroIsbn.isBlank()) {
            throw new IllegalArgumentException("O ISBN do livro é obrigatório");
        }
        if (livroTitulo.length() > 255 || livroAutor.length() > 150 || livroIsbn.length() > 13) {
            throw new IllegalArgumentException("Os dados do livro excedem o tamanho permitido");
        }
        if (dataEmprestimo == null || dataPrevistaDevolucao == null) {
            throw new IllegalArgumentException("As datas de empréstimo e de devolução prevista são obrigatórias");
        }
        if (dataPrevistaDevolucao.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("A devolução prevista não pode ser anterior ao empréstimo");
        }
        if (dataDevolucao != null && dataDevolucao.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("A devolução não pode ser anterior ao empréstimo");
        }
        return new EmprestimoLeitura(emprestimoId, livroId, livroTitulo, livroAutor, livroIsbn,
                usuarioId, dataEmprestimo, dataPrevistaDevolucao, dataDevolucao);
    }

    public static EmprestimoLeitura reconstituir(UUID emprestimoId, UUID livroId, String livroTitulo,
                                                 String livroAutor, String livroIsbn, UUID usuarioId,
                                                 LocalDate dataEmprestimo,
                                                 LocalDate dataPrevistaDevolucao,
                                                 LocalDate dataDevolucao) {
        return new EmprestimoLeitura(emprestimoId, livroId, livroTitulo, livroAutor, livroIsbn,
                usuarioId, dataEmprestimo, dataPrevistaDevolucao, dataDevolucao);
    }

    public void registrarDevolucao(LocalDate data) {
        if (estaDevolvido()) {
            return;
        }
        if (data == null) {
            throw new IllegalArgumentException("A data de devolução é obrigatória");
        }
        if (data.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("A devolução não pode ser anterior ao empréstimo");
        }
        this.dataDevolucao = data;
    }

    public boolean estaDevolvido() {
        return dataDevolucao != null;
    }

    public boolean estaEmAberto() {
        return dataDevolucao == null;
    }

    public boolean estaAtrasado(LocalDate hoje) {
        if (hoje == null) {
            throw new IllegalArgumentException("A data atual é obrigatória");
        }
        return estaEmAberto() && hoje.isAfter(dataPrevistaDevolucao);
    }

    public UUID getEmprestimoId() {
        return emprestimoId;
    }

    public UUID getLivroId() {
        return livroId;
    }

    public String getLivroTitulo() {
        return livroTitulo;
    }

    public String getLivroAutor() {
        return livroAutor;
    }

    public String getLivroIsbn() {
        return livroIsbn;
    }

    public UUID getUsuarioId() {
        return usuarioId;
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
}
