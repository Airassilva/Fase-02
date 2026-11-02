package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroUpdateRequest;
import dev.aira.bibliotecanucleo.livro.application.port.in.AtualizarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.BuscarLivrosUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.DevolverExemplarUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.ExcluirLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.RetirarExemplarUseCase;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LivroControllerTest {
    private CadastrarLivroUseCase cadastrar;
    private BuscarLivrosUseCase buscar;
    private AtualizarLivroUseCase atualizar;
    private ExcluirLivroUseCase excluir;
    private RetirarExemplarUseCase reservar;
    private DevolverExemplarUseCase devolver;
    private LivroController controller;
    private Livro livro;

    @BeforeEach
    void setUp() {
        cadastrar = mock(CadastrarLivroUseCase.class);
        buscar = mock(BuscarLivrosUseCase.class);
        atualizar = mock(AtualizarLivroUseCase.class);
        excluir = mock(ExcluirLivroUseCase.class);
        reservar = mock(RetirarExemplarUseCase.class);
        devolver = mock(DevolverExemplarUseCase.class);
        controller = new LivroController(cadastrar, buscar, atualizar, excluir, reservar, devolver, new LivroWebMapper());
        livro = Livro.cadastrar("Título", "Autor", Isbn.de("9780306406157"), 2, "Descrição válida");
    }

    @Test
    void cadastraLivroERetornaCreated() {
        when(cadastrar.cadastrarLivro(any())).thenReturn(livro);

        var response = controller.cadastrarLivro(new LivroRequest("Título", "Autor", "9780306406157", 2, "Descrição válida"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(livro.getId(), response.getBody().id());
        verify(cadastrar).cadastrarLivro(any());
    }

    @Test
    void listaLivrosComFiltrosEPaginacao() {
        var filtro = new LivroFiltro("Tít", "Autor", "978030");
        var pageQuery = new PageQuery(1, 5);
        when(buscar.buscarLivros(filtro, pageQuery))
                .thenReturn(new PageResult<>(List.of(livro), 12, 1, 5));

        var response = controller.buscarLivros(
                filtro.nome(), filtro.autor(), filtro.isbn(), PageRequest.of(1, 5));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().content().size());
        assertEquals(livro.getId(), response.getBody().content().get(0).id());
        assertEquals(12, response.getBody().total());
        assertEquals(1, response.getBody().page());
        assertEquals(5, response.getBody().size());
        verify(buscar).buscarLivros(filtro, pageQuery);
    }

    @Test
    void listaSemResultadosRetornaPaginaVazia() {
        var pageQuery = new PageQuery(0, 20);
        when(buscar.buscarLivros(any(), any()))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 20));

        var response = controller.buscarLivros(null, null, null, PageRequest.of(0, 20));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody().content());
        assertEquals(0, response.getBody().total());
        verify(buscar).buscarLivros(new LivroFiltro(null, null, null), pageQuery);
    }

    @Test
    void buscaLivroPorId() {
        when(buscar.buscarLivroPorId(livro.getId())).thenReturn(livro);

        var response = controller.buscarLivroPorId(livro.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(livro.getId(), response.getBody().id());
    }

    @Test
    void atualizaLivro() {
        when(atualizar.atualizarLivro(livro.getId(), 4, "Nova descrição válida")).thenReturn(livro);

        var response = controller.atualizarLivro(livro.getId(), new LivroUpdateRequest(4, "Nova descrição válida"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(atualizar).atualizarLivro(livro.getId(), 4, "Nova descrição válida");
    }

    @Test
    void reservaExemplar() {
        when(reservar.reservarExemplar(livro.getId())).thenReturn(livro);

        var response = controller.reservarExemplar(livro.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(livro.getId(), response.getBody().id());
    }

    @Test
    void devolveExemplar() {
        when(devolver.devolverExemplar(livro.getId())).thenReturn(livro);

        var response = controller.devolverExemplar(livro.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(livro.getId(), response.getBody().id());
    }

    @Test
    void excluiLivroERetornaNoContent() {
        UUID id = livro.getId();

        var response = controller.excluirLivro(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(excluir).excluirLivro(id);
    }
}
