package dev.aira.bibliotecanucleo.livro.application.service;

import dev.aira.bibliotecanucleo.livro.application.exception.LivroJaCadastradoException;
import dev.aira.bibliotecanucleo.livro.application.port.in.AtualizarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.BuscarLivrosUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.CadastrarLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.DevolverExemplarUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.ExcluirLivroUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.in.RetirarExemplarUseCase;
import dev.aira.bibliotecanucleo.livro.application.port.out.LivroRepositoryPort;
import dev.aira.bibliotecanucleo.livro.application.query.LivroFiltro;
import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import dev.aira.bibliotecanucleo.shared.model.PageQuery;
import dev.aira.bibliotecanucleo.shared.model.PageResult;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LivroService implements CadastrarLivroUseCase, BuscarLivrosUseCase, RetirarExemplarUseCase, DevolverExemplarUseCase, AtualizarLivroUseCase, ExcluirLivroUseCase {

    private final LivroRepositoryPort livroRepositoryPort;

    public LivroService(LivroRepositoryPort livroRepositoryPort) {
        this.livroRepositoryPort = livroRepositoryPort;
    }

    @Override
    @Transactional
    public Livro cadastrarLivro(CadastrarLivroCommand command) {
        Isbn isbn = Isbn.de(command.isbn());

        if (livroRepositoryPort.existePorIsbn(isbn)) {
            throw new LivroJaCadastradoException(isbn.valor());
        }

        Livro livro = Livro.cadastrar(
                command.titulo(),
                command.autor(),
                isbn,
                command.quantidadeTotal(),
                command.descricao());

        return livroRepositoryPort.salvar(livro);
    }

    @Override
    @Transactional
    public Livro atualizarLivro(UUID id, Integer quantidadeTotal, String descricao) {
       var livro = buscarLivroPorId(id);
       livro.atualizar(quantidadeTotal, descricao);
       return livroRepositoryPort.salvar(livro);
    }

    @Override
    @Transactional
    public PageResult<Livro> buscarLivros(LivroFiltro filtro, PageQuery pageQuery) {
        return livroRepositoryPort.buscar(filtro, pageQuery);
    }

    @Override
    @Transactional
    public Livro buscarLivroPorId(UUID id) {
        return livroRepositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional
    public Livro reservarExemplar(UUID id) {
        var livro = buscarLivroPorId(id);
        livro.reservarExemplar();
        return livroRepositoryPort.salvar(livro);
    }

    @Override
    @Transactional
    public void excluirLivro(UUID id) {
        livroRepositoryPort.excluir(id);
    }

    @Override
    public Livro devolverExemplar(UUID id) {
        var livro = buscarLivroPorId(id);
        livro.devolverExemplar();
        return livroRepositoryPort.salvar(livro);
    }
}
