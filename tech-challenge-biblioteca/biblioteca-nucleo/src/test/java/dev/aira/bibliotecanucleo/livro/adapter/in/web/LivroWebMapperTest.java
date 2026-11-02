package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroResponse;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase.CadastrarLivroCommand;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivroWebMapperTest {
    private final LivroWebMapper mapper = new LivroWebMapper();

    @Test
    void converteRequisicaoEmComandoDeCadastro() {
        LivroRequest request = new LivroRequest("Título", "Autor", "9780306406157", 3, "Descrição válida");

        CadastrarLivroCommand command = mapper.toCommand(request);

        assertEquals("Título", command.titulo());
        assertEquals("Autor", command.autor());
        assertEquals("9780306406157", command.isbn());
        assertEquals(3, command.quantidadeTotal());
        assertEquals("Descrição válida", command.descricao());
    }

    @Test
    void converteLivroEmResposta() {
        Livro livro = Livro.cadastrar("Título", "Autor", Isbn.de("9780306406157"), 2, "Descrição válida");

        LivroResponse response = mapper.toResponse(livro);

        assertEquals(livro.getId(), response.id());
        assertEquals("Título", response.titulo());
        assertEquals("Autor", response.autor());
        assertEquals("9780306406157", response.isbn());
        assertEquals(2, response.quantidadeTotal());
        assertEquals(2, response.quantidadeDisponivel());
        assertTrue(response.disponivel());
        assertEquals("Descrição válida", response.descricao());
    }

    @Test
    void convertePaginaDeLivrosPreservandoMetadados() {
        Livro primeiro = Livro.cadastrar("Primeiro", "Autor", Isbn.de("9780306406157"), 1, "Descrição válida");
        Livro segundo = Livro.cadastrar("Segundo", "Autor", Isbn.de("9781861972712"), 2, "Outra descrição válida");
        PageResult<Livro> pagina = new PageResult<>(List.of(primeiro, segundo), 23, 2, 10);

        PageResult<LivroResponse> responses = mapper.toResponsePage(pagina);

        assertEquals(List.of("Primeiro", "Segundo"),
                responses.content().stream().map(LivroResponse::titulo).toList());
        assertEquals(23, responses.total());
        assertEquals(2, responses.page());
        assertEquals(10, responses.size());
    }

    @Test
    void convertePaginaVaziaPreservandoMetadados() {
        PageResult<LivroResponse> responses = mapper.toResponsePage(new PageResult<>(List.of(), 0, 0, 10));

        assertEquals(List.of(), responses.content());
        assertEquals(0, responses.total());
        assertEquals(0, responses.page());
        assertEquals(10, responses.size());
    }
}
