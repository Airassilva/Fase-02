package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroResponse;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase.CadastrarLivroCommand;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class LivroWebMapper {

    public CadastrarLivroCommand toCommand(@Valid LivroRequest livroRequest) {
        return new CadastrarLivroCommand(
                livroRequest.titulo(),
                livroRequest.autor(),
                livroRequest.isbn(),
                livroRequest.quantidadeTotal(),
                livroRequest.descricao()
        );
    }

    public LivroResponse toResponse(Livro livro) {
        return  new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn().toString(),
                livro.getQuantidadeTotal(),
                livro.getQuantidadeDisponivel(),
                livro.isDisponivel(),
                livro.getDescricao()
        );
    }

    public PageResult<LivroResponse> toResponsePage(PageResult<Livro> livros) {
        return new PageResult<>(
                livros.content().stream().map(this::toResponse).toList(),
                livros.total(),
                livros.page(),
                livros.size()
        );
    }
}
