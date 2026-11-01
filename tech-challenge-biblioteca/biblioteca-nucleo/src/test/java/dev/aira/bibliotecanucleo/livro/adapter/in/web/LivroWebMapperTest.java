package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroResponse;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase.CadastrarLivroCommand;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
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
    void converteTodosOsLivrosEmRespostas() {
        Livro primeiro = Livro.cadastrar("Primeiro", "Autor", Isbn.de("9780306406157"), 1, "Descrição válida");
        Livro segundo = Livro.cadastrar("Segundo", "Autor", Isbn.de("9781861972712"), 2, "Outra descrição válida");

        List<LivroResponse> responses = mapper.toResponseList(List.of(primeiro, segundo));

        assertEquals(List.of("Primeiro", "Segundo"), responses.stream().map(LivroResponse::titulo).toList());
    }

    @Test
    void converteListaVaziaSemLancarExcecao() {
        assertEquals(List.of(), mapper.toResponseList(List.of()));
    }
}
