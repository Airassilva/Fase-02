package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import dev.aira.bibliotecanucleo.livro.application.exception.LivroNaoEncontradoException;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LivroPersistenceAdapterTest {
    private LivroJpaRepository jpaRepository;
    private LivroPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(LivroJpaRepository.class);
        adapter = new LivroPersistenceAdapter(jpaRepository, new LivroPersistenceMapper());
    }

    @Test
    void verificaExistenciaPorIsbnNormalizado() {
        Isbn isbn = Isbn.de("9780306406157");
        when(jpaRepository.existsByIsbn(isbn.valor())).thenReturn(true);

        assertTrue(adapter.existePorIsbn(isbn));
        verify(jpaRepository).existsByIsbn("9780306406157");
    }

    @Test
    void salvaEConverteEntidadeParaDominio() {
        Livro livro = livro();
        when(jpaRepository.save(org.mockito.ArgumentMatchers.any(LivroJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Livro salvo = adapter.salvar(livro);

        assertEquals(livro.getId(), salvo.getId());
        assertEquals(livro.getIsbn(), salvo.getIsbn());
        assertEquals(livro.getQuantidadeDisponivel(), salvo.getQuantidadeDisponivel());
    }

    @Test
    void buscaPorIdEConverteParaDominio() {
        Livro livro = livro();
        when(jpaRepository.findById(livro.getId())).thenReturn(Optional.of(toEntity(livro)));

        Livro encontrado = adapter.buscarPorId(livro.getId());

        assertEquals(livro.getId(), encontrado.getId());
        assertEquals(livro.getTitulo(), encontrado.getTitulo());
    }

    @Test
    void lancaExcecaoQuandoIdNaoExiste() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LivroNaoEncontradoException.class, () -> adapter.buscarPorId(id));
    }

    @Test
    void excluiLivroExistente() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.existsById(id)).thenReturn(true);

        adapter.excluir(id);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    void naoExcluiLivroInexistente() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.existsById(id)).thenReturn(false);

        assertThrows(LivroNaoEncontradoException.class, () -> adapter.excluir(id));

        verify(jpaRepository, never()).deleteById(id);
    }

    @Test
    void listaLivrosConvertendoEntidadesParaDominio() {
        Livro livro = livro();
        when(jpaRepository.findAll()).thenReturn(List.of(toEntity(livro)));

        List<Livro> livros = adapter.buscarTodos();

        assertEquals(1, livros.size());
        assertEquals(livro.getId(), livros.get(0).getId());
    }

    @Test
    void retornaListaVaziaQuandoNaoHaLivros() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), adapter.buscarTodos());
    }

    private Livro livro() {
        return Livro.cadastrar("Título", "Autor", Isbn.de("9780306406157"), 2, "Descrição válida");
    }

    private LivroJpaEntity toEntity(Livro livro) {
        return new LivroJpaEntity(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getIsbn().valor(),
                livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel(), livro.getDescricao());
    }
}
