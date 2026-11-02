package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroUpdateRequest;
import dev.aira.bibliotecanucleo.livro.application.port.in.AtualizarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.BuscarLivrosUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.DevolverExemplarUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.ExcluirLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.RetirarExemplarUseCase;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void listaLivrosERetornaLista() {
        when(buscar.buscarLivros()).thenReturn(List.of(livro));

        var response = controller.buscarLivros();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(livro.getId(), response.getBody().get(0).id());
    }

    @Test
    void listaVaziaRetornaSucessoComListaVazia() {
        when(buscar.buscarLivros()).thenReturn(List.of());

        var response = controller.buscarLivros();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
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
