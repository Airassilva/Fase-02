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
@RequestMapping("api/biblioteca/livros")
public class LivroController {

    private final CadastrarLivroUseCase cadastrarLivro;
    private final BuscarLivrosUseCase buscarLivrosUseCase;
    private final AtualizarLivroUseCase atualizarLivroUseCase;
    private final ExcluirLivroUseCase excluirLivroUseCase;
    private final LivroWebMapper mapper;

    @PostMapping
    public ResponseEntity<LivroResponse> cadastrarLivro(@Valid @RequestBody LivroRequest livroRequest) {
        var livro = cadastrarLivro.cadastrarLivro(mapper.toCommand(livroRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(livro));
    }

    @GetMapping
    public ResponseEntity<PageResult<LivroResponse>> buscarLivros(@RequestParam(required = false) String nome, @RequestParam(required = false) String autor, @RequestParam(required = false) String isbn, Pageable pageable) {
        var filtro = new LivroFiltro(nome, autor, isbn);
        var pageQuery = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        var livros = buscarLivrosUseCase.buscarLivros(filtro, pageQuery);
        return ResponseEntity.ok(mapper.toResponsePage(livros));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscarLivroPorId(@PathVariable UUID id) {
        var livro = buscarLivrosUseCase.buscarLivroPorId(id);
        return ResponseEntity.ok().body(mapper.toResponse(livro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizarLivro(@PathVariable UUID id, @Valid @RequestBody LivroUpdateRequest livroRequest) {
        var livro = atualizarLivroUseCase.atualizarLivro(id, livroRequest.quantidadeTotal(), livroRequest.descricao());
        return ResponseEntity.ok().body(mapper.toResponse(livro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirLivro(@PathVariable UUID id) {
        excluirLivroUseCase.excluirLivro(id);
        return ResponseEntity.noContent().build();
    }
}
