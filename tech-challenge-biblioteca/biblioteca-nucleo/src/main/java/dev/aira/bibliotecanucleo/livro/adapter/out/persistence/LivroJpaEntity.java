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

    @Column(name = "quantidade_total", nullable = false)
    private int quantidadeTotal;

    @Column(name = "quantidade_disponivel", nullable = false)
    private int quantidadeDisponivel;

    protected LivroJpaEntity() {
    }

    LivroJpaEntity(UUID id, String titulo, String autor, String isbn, int quantidadeTotal, int quantidadeDisponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
}
