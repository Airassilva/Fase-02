package dev.aira.bibliotecanucleo.livro.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivroTest {
    private static final Isbn ISBN = Isbn.de("9780306406157");

    @Test
    void cadastraLivroComTodosOsExemplaresDisponiveis() {
        Livro livro = Livro.cadastrar("  Clean Code  ", "  Robert Martin  ", ISBN, 3, "  Descrição do livro  ");

        assertEquals("Clean Code", livro.getTitulo());
        assertEquals("Robert Martin", livro.getAutor());
        assertEquals(ISBN, livro.getIsbn());
        assertEquals(3, livro.getQuantidadeTotal());
        assertEquals(3, livro.getQuantidadeDisponivel());
        assertEquals(0, livro.quantidadeEmprestada());
        assertTrue(livro.isDisponivel());
        assertEquals("Descrição do livro", livro.getDescricao());
    }

    @Test
    void reconstituiLivroECalculaExemplaresEmprestados() {
        UUID id = UUID.randomUUID();
        Livro livro = Livro.reconstituir(id, "Clean Code", "Robert Martin", ISBN, 3, 1, "Descrição do livro");

        assertEquals(id, livro.getId());
        assertEquals(2, livro.quantidadeEmprestada());
        assertTrue(livro.isDisponivel());
    }

    @Test
    void rejeitaDadosObrigatoriosInvalidos() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(null, "Título", "Autor", ISBN, 1, 1, "Descrição válida para livro"));
        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, " ", "Autor", ISBN, 1, 1, "Descrição válida para livro"));
        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, "Título", " ", ISBN, 1, 1, "Descrição válida para livro"));
        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, "Título", "Autor", null, 1, 1, "Descrição válida para livro"));
        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, "Título", "Autor", ISBN, 1, 1, " "));
    }

    @Test
    void rejeitaQuantidadesInvalidasAoReconstituir() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, "Título", "Autor", ISBN, 0, 0, "Descrição válida para livro"));
        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, "Título", "Autor", ISBN, 2, -1, "Descrição válida para livro"));
        assertThrows(IllegalArgumentException.class,
                () -> Livro.reconstituir(id, "Título", "Autor", ISBN, 2, 3, "Descrição válida para livro"));
    }

    @Test
    void atualizaTotalPreservandoExemplaresEmprestados() {
        Livro livro = Livro.reconstituir(UUID.randomUUID(), "Título", "Autor", ISBN, 4, 2, "Descrição antiga");

        livro.atualizar(5, "  Nova descrição  ");

        assertEquals(5, livro.getQuantidadeTotal());
        assertEquals(3, livro.getQuantidadeDisponivel());
        assertEquals(2, livro.quantidadeEmprestada());
        assertEquals("Nova descrição", livro.getDescricao());
    }

    @Test
    void rejeitaAtualizacaoComDescricaoVaziaOuTotalMenorQueEmprestimos() {
        Livro livro = Livro.reconstituir(UUID.randomUUID(), "Título", "Autor", ISBN, 4, 2, "Descrição antiga");

        assertThrows(IllegalArgumentException.class, () -> livro.atualizar(5, " "));
        assertThrows(IllegalArgumentException.class, () -> livro.atualizar(0, "Descrição válida"));
        assertThrows(IllegalArgumentException.class, () -> livro.atualizar(1, "Descrição válida"));
    }

    @Test
    void reservaExemplarEIndisponibilizaLivroQuandoAcabam() {
        Livro livro = Livro.cadastrar("Título", "Autor", ISBN, 1, "Descrição válida");

        livro.reservarExemplar();

        assertEquals(0, livro.getQuantidadeDisponivel());
        assertFalse(livro.isDisponivel());
        assertThrows(LivroIndisponivelException.class, livro::reservarExemplar);
    }

    @Test
    void devolveExemplarERejeitaDevolucaoQuandoTodosEstaoDisponiveis() {
        Livro livro = Livro.reconstituir(UUID.randomUUID(), "Título", "Autor", ISBN, 2, 1, "Descrição válida");

        livro.devolverExemplar();

        assertEquals(2, livro.getQuantidadeDisponivel());
        assertThrows(IllegalStateException.class, livro::devolverExemplar);
    }
}
