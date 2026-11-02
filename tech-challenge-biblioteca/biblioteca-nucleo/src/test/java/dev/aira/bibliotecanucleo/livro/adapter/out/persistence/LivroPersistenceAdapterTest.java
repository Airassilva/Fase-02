package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import dev.aira.bibliotecanucleo.livro.application.exception.LivroNaoEncontradoException;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

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
    void listaLivrosConvertendoEntidadesEPreservandoPaginacao() {
        Livro livro = livro();
        var pageable = PageRequest.of(1, 5);
        when(jpaRepository.findAll(org.mockito.ArgumentMatchers.any(
                org.springframework.data.jpa.domain.Specification.class), org.mockito.ArgumentMatchers.eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(toEntity(livro)), pageable, 12));

        PageResult<Livro> page = adapter.buscar(new LivroFiltro("Tít", "Autor", "978030"), new PageQuery(1, 5));

        assertEquals(1, page.content().size());
        assertEquals(livro.getId(), page.content().get(0).getId());
        assertEquals(12, page.total());
        assertEquals(1, page.page());
        assertEquals(5, page.size());
    }

    @Test
    void retornaPaginaVaziaQuandoNaoHaLivros() {
        var pageable = PageRequest.of(0, 10);
        when(jpaRepository.findAll(org.mockito.ArgumentMatchers.any(
                org.springframework.data.jpa.domain.Specification.class), org.mockito.ArgumentMatchers.eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        assertEquals(new PageResult<>(List.of(), 0, 0, 10),
                adapter.buscar(new LivroFiltro(null, null, null), new PageQuery(0, 10)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void aplicaFiltrosDeNomeAutorEIsbnComBuscaParcialSemDiferenciarMaiusculas() {
        Root<LivroJpaEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<String> titulo = mock(Path.class);
        Path<String> autor = mock(Path.class);
        Path<String> isbn = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        Predicate resultado = mock(Predicate.class);

        doReturn(titulo).when(root).get("titulo");
        doReturn(autor).when(root).get("autor");
        doReturn(isbn).when(root).get("isbn");
        when(criteriaBuilder.lower(titulo)).thenReturn(titulo);
        when(criteriaBuilder.lower(autor)).thenReturn(autor);
        when(criteriaBuilder.lower(isbn)).thenReturn(isbn);
        when(criteriaBuilder.like(any(Expression.class), anyString(), anyChar())).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(resultado);

        Predicate actual = adapter.comFiltro(new LivroFiltro(" Clean%_ ", "Mar_tin", "978-0 306"))
                .toPredicate(root, query, criteriaBuilder);

        assertEquals(resultado, actual);
        var patterns = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(criteriaBuilder, times(3)).like(any(Expression.class), patterns.capture(), eq('\\'));
        assertEquals(List.of("%clean\\%\\_%", "%mar\\_tin%", "%9780306%"), patterns.getAllValues());
        verify(criteriaBuilder).and(any(Predicate[].class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void ignoraFiltrosNulosOuEmBranco() {
        Root<LivroJpaEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate resultado = mock(Predicate.class);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(resultado);

        Predicate actual = adapter.comFiltro(new LivroFiltro(" ", null, ""))
                .toPredicate(root, query, criteriaBuilder);

        assertEquals(resultado, actual);
        verify(criteriaBuilder, never()).like(any(Expression.class), anyString(), anyChar());
        verify(criteriaBuilder).and(any(Predicate[].class));
    }

    private Livro livro() {
        return Livro.cadastrar("Título", "Autor", Isbn.de("9780306406157"), 2, "Descrição válida");
    }

    private LivroJpaEntity toEntity(Livro livro) {
        return new LivroJpaEntity(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getIsbn().valor(),
                livro.getQuantidadeTotal(), livro.getQuantidadeDisponivel(), livro.getDescricao());
    }
}
