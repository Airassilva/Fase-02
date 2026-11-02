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
        this.id = id;
        this.titulo = titulo.strip();
        this.autor = autor.strip();
        this.isbn = isbn;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.descricao = descricao.strip();
    }

    public static Livro cadastrar(String titulo, String autor, Isbn isbn, int quantidadeTotal, String descricao) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("Autor é obrigatório");
        }
        if (isbn == null) {
            throw new IllegalArgumentException("ISBN é obrigatório");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (quantidadeTotal < 1) {
            throw new IllegalArgumentException("Deve haver ao menos um exemplar");
        }
        return new Livro(null, titulo.strip(), autor.strip(), isbn, quantidadeTotal, quantidadeTotal, descricao.strip());
    }

    public static Livro reconstituir(UUID id, String titulo, String autor, Isbn isbn, int quantidadeTotal, int quantidadeDisponivel, String descricao) {
        return new Livro(id, titulo, autor, isbn, quantidadeTotal, quantidadeDisponivel, descricao);
    }

    public void atualizar(int novaQuantidadeTotal, String novaDescricao) {
        if (novaDescricao == null || novaDescricao.isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória");
        }

        int quantidadeEmprestada = quantidadeEmprestada();
        if (novaQuantidadeTotal < 1 || novaQuantidadeTotal < quantidadeEmprestada) {
            throw new IllegalArgumentException(
                    "A quantidade total não pode ser menor que o número de exemplares emprestados");
        }

        this.quantidadeTotal = novaQuantidadeTotal;
        this.quantidadeDisponivel = novaQuantidadeTotal - quantidadeEmprestada;
        this.descricao = novaDescricao.strip();
    }

    public int quantidadeEmprestada() {
        return quantidadeTotal - quantidadeDisponivel;
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
