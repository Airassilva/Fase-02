package dev.aira.bibliotecanucleo.livro.domain.model;

import java.util.UUID;

public class Livro {
    private UUID id;
    private String titulo;
    private String autor;
    private Isbn isbn;
    private int quantidadeTotal;
    private int quantidadeDisponivel;
    private String descricao;

    private Livro(UUID id, String titulo, String autor, Isbn isbn, int quantidadeTotal, int quantidadeDisponivel, String descricao) {
        if (id == null || titulo == null || titulo.isBlank() || autor == null || autor.isBlank() || isbn == null || descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Os dados do livro são obrigatórios");
        }
        if (quantidadeTotal < 1 || quantidadeDisponivel < 0 || quantidadeDisponivel > quantidadeTotal) {
            throw new IllegalArgumentException("As quantidades do livro são inválidas");
        }
        this.id = id;
        this.titulo = titulo.strip();
        this.autor = autor.strip();
        this.isbn = isbn;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.descricao = descricao.strip();
    }

    public static Livro cadastrar(String titulo, String autor, Isbn isbn, int quantidadeTotal, String descricao) {
        return new Livro(UUID.randomUUID(), titulo, autor, isbn, quantidadeTotal, quantidadeTotal, descricao);
    }

    public static Livro reconstituir(UUID id, String titulo, String autor, Isbn isbn, int quantidadeTotal, int quantidadeDisponivel, String descricao) {
        return new Livro(id, titulo, autor, isbn, quantidadeTotal, quantidadeDisponivel, descricao);
    }

    public void atualizar(String titulo, String autor, Isbn isbn, int quantidadeTotal, String descricao) {
        int quantidadeEmprestada = this.quantidadeTotal - this.quantidadeDisponivel;
        if (quantidadeTotal < 1 || quantidadeTotal < quantidadeEmprestada) {
            throw new IllegalArgumentException(
                    "A quantidade total não pode ser menor que o número de exemplares emprestados");
        }
        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank() || isbn == null || descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Os dados do livro são obrigatórios");
        }
        this.titulo = titulo.strip();
        this.autor = autor.strip();
        this.isbn = isbn;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal - quantidadeEmprestada;
        this.descricao = descricao.strip();
    }

    public void reservarExemplar() {
        if (quantidadeDisponivel <= 0) {
            throw new LivroIndisponivelException(id);
        }
        quantidadeDisponivel--;
    }

    public void devolverExemplar() {
        if (quantidadeDisponivel >= quantidadeTotal) {
            throw new IllegalStateException("Todos os exemplares já estão disponíveis");
        }
        quantidadeDisponivel++;
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public Isbn getIsbn() {
        return isbn;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public boolean isDisponivel() {
        return quantidadeDisponivel > 0;
    }

    public String getDescricao() {
        return descricao;
    }
}
