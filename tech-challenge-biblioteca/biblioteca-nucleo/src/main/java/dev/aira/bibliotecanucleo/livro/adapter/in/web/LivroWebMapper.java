package dev.aira.bibliotecanucleo.livro.adapter.in.web;

import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroRequest;
import dev.aira.bibliotecanucleo.livro.adapter.in.web.dto.LivroResponse;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase.CadastrarLivroCommand;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public List<LivroResponse> toResponseList(List<Livro> livros) {
        return livros.stream().map(this::toResponse).toList();
    }
}
