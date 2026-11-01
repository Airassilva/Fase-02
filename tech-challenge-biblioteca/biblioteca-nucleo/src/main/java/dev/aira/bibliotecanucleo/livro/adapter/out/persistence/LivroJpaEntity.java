package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "livros")
public class LivroJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 255)
    private String autor;

    @Column(nullable = false, length = 13, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int quantidadeTotal;

    @Column(nullable = false)
    private int quantidadeDisponivel;

    @Column(length = 500)
    private String descricao;

    protected LivroJpaEntity() {
    }

    LivroJpaEntity(UUID id, String titulo, String autor, String isbn, int quantidadeTotal, int quantidadeDisponivel, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.descricao = descricao;
    }
}
