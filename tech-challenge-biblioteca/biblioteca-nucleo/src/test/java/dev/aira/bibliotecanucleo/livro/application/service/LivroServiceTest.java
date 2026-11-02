package dev.aira.bibliotecanucleo.livro.application.service;

import dev.aira.bibliotecanucleo.livro.application.exception.LivroJaCadastradoException;
import dev.aira.bibliotecanucleo.livro.application.exception.LivroNaoEncontradoException;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase.CadastrarLivroCommand;
import dev.aira.bibliotecanucleo.livro.application.port.out.LivroRepositoryPort;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class LivroServiceTest {
    private LivroRepositoryPort repository;
    private LivroService service;

    @BeforeEach
    void setUp() {
        repository = mock(LivroRepositoryPort.class);
        service = new LivroService(repository);
    }

    @Test
    void cadastraLivroQuandoIsbnAindaNaoExiste() {
        CadastrarLivroCommand command = command();
        when(repository.existePorIsbn(any(Isbn.class))).thenReturn(false);
        when(repository.salvar(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Livro resultado = service.cadastrarLivro(command);

        assertEquals(command.titulo(), resultado.getTitulo());
        assertEquals(command.autor(), resultado.getAutor());
        assertEquals(command.quantidadeTotal(), resultado.getQuantidadeDisponivel());
        verify(repository).salvar(any(Livro.class));
    }

    @Test
    void rejeitaCadastroComIsbnDuplicado() {
        CadastrarLivroCommand command = command();
        when(repository.existePorIsbn(any(Isbn.class))).thenReturn(true);

        assertThrows(LivroJaCadastradoException.class, () -> service.cadastrarLivro(command));

        verify(repository, never()).salvar(any(Livro.class));
    }

    @Test
    void atualizaLivroExistente() {
        Livro livro = livro();
        when(repository.buscarPorId(livro.getId())).thenReturn(livro);
        when(repository.salvar(livro)).thenReturn(livro);

        Livro resultado = service.atualizarLivro(livro.getId(), 5, "Descrição atualizada");

        assertEquals(5, resultado.getQuantidadeTotal());
        assertEquals("Descrição atualizada", resultado.getDescricao());
        verify(repository).salvar(livro);
    }

    @Test
    void listaLivrosComFiltroEPaginacao() {
        LivroFiltro filtro = new LivroFiltro("Clean", "Martin", "9780");
        PageQuery pageQuery = new PageQuery(1, 10);
        PageResult<Livro> pagina = new PageResult<>(java.util.List.of(livro()), 11, 1, 10);
        when(repository.buscar(filtro, pageQuery)).thenReturn(pagina);

        assertEquals(pagina, service.buscarLivros(filtro, pageQuery));
        verify(repository).buscar(filtro, pageQuery);
    }

    @Test
    void buscaLivroPorId() {
        Livro livro = livro();
        when(repository.buscarPorId(livro.getId())).thenReturn(livro);

        assertEquals(livro, service.buscarLivroPorId(livro.getId()));
    }

    @Test
    void excluiLivro() {
        UUID id = UUID.randomUUID();

        service.excluirLivro(id);

        verify(repository).excluir(id);
    }

    @Test
    void propagaLivroNaoEncontradoDuranteBuscaEAtualizacao() {
        UUID id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenThrow(new LivroNaoEncontradoException(id));

        assertThrows(LivroNaoEncontradoException.class, () -> service.buscarLivroPorId(id));
        assertThrows(LivroNaoEncontradoException.class, () -> service.atualizarLivro(id, 1, "Descrição válida"));
    }

    private CadastrarLivroCommand command() {
        return new CadastrarLivroCommand("Título", "Autor", "9780306406157", 2, "Descrição válida");
    }

    private Livro livro() {
        return Livro.cadastrar("Título", "Autor", Isbn.de("9780306406157"), 2, "Descrição válida");
    }
}
