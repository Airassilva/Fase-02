package dev.aira.bibliotecanucleo.emprestimo.adapter.out.livro;

import dev.aira.bibliotecanucleo.emprestimo.application.port.out.LivroGateway;
import dev.aira.bibliotecanucleo.livro.application.port.out.LivroRepositoryPort;

import java.util.UUID;

public class LivroGatewayAdapter implements LivroGateway {
    private final LivroRepositoryPort livroRepository;

    public LivroGatewayAdapter(LivroRepositoryPort livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Override
    public LivroInfo buscarLivro(UUID livroId) {
        var livro = livroRepository.buscarPorId(livroId);
        return new LivroInfo(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn().valor());
    }

    @Override
    public boolean retirarExemplar(UUID livroId) {
        return livroRepository.retirarExemplar(livroId);
    }

    @Override
    public void devolverExemplar(UUID livroId) {
        livroRepository.devolverExemplar(livroId);
    }
}
