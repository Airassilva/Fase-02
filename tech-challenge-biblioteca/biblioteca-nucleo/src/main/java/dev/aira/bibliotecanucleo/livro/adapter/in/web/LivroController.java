package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroResponse;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroUpdateRequest;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.livro.application.port.in.AtualizarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.BuscarLivrosUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.ExcluirLivroUseCase;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Cadastro e consulta do acervo")
@RequestMapping("api/biblioteca/livros")
public class LivroController {

    private final CadastrarLivroUseCase cadastrarLivro;
    private final BuscarLivrosUseCase buscarLivrosUseCase;
    private final AtualizarLivroUseCase atualizarLivroUseCase;
    private final ExcluirLivroUseCase excluirLivroUseCase;
    private final LivroWebMapper mapper;

    @Operation(summary = "Cadastra um livro",
            description = "O ISBN pode vir em formato 10 ou 13 e é armazenado como ISBN-13.")
    @ApiResponse(responseCode = "201", description = "Livro cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "ISBN já cadastrado")
    @PostMapping
    public ResponseEntity<LivroResponse> cadastrarLivro(@Valid @RequestBody LivroRequest livroRequest) {
        var livro = cadastrarLivro.cadastrarLivro(mapper.toCommand(livroRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(livro));
    }

    @Operation(summary = "Busca livros", description = "Lista livros com filtros opcionais e paginação.")
    @ApiResponse(responseCode = "200", description = "Livros encontrados")
    @GetMapping
    public ResponseEntity<PageResult<LivroResponse>> buscarLivros(@RequestParam(required = false) String nome, @RequestParam(required = false) String autor, @RequestParam(required = false) String isbn, Pageable pageable) {
        var filtro = new LivroFiltro(nome, autor, isbn);
        var pageQuery = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        var livros = buscarLivrosUseCase.buscarLivros(filtro, pageQuery);
        return ResponseEntity.ok(mapper.toResponsePage(livros));
    }

    @Operation(summary = "Busca um livro por ID")
    @ApiResponse(responseCode = "200", description = "Livro encontrado")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarLivroPorId(@PathVariable UUID id) {
        var livro = buscarLivrosUseCase.buscarLivroPorId(id);
        return ResponseEntity.ok().body(mapper.toResponse(livro));
    }

    @Operation(summary = "Atualiza um livro", description = "Atualiza a quantidade total e a descrição do livro.")
    @ApiResponse(responseCode = "200", description = "Livro atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizarLivro(@PathVariable UUID id, @Valid @RequestBody LivroUpdateRequest livroRequest) {
        var livro = atualizarLivroUseCase.atualizarLivro(id, livroRequest.quantidadeTotal(), livroRequest.descricao());
        return ResponseEntity.ok().body(mapper.toResponse(livro));
    }

    @Operation(summary = "Exclui um livro por ID")
    @ApiResponse(responseCode = "204", description = "Livro excluído")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirLivro(@PathVariable UUID id) {
        excluirLivroUseCase.excluirLivro(id);
        return ResponseEntity.noContent().build();
    }
}
