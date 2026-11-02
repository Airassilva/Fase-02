package dev.aira.bibliotecanucleo.livro.adapter.out.persistence;

import dev.aira.bibliotecanucleo.livro.domain.model.Isbn;
import dev.aira.bibliotecanucleo.livro.domain.model.Livro;
import org.springframework.stereotype.Component;

@Component
public class LivroPersistenceMapper {

    public LivroJpaEntity toEntity(Livro livro) {
        return new LivroJpaEntity(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getIsbn().valor(),
                livro.getQuantidadeTotal(),
                livro.getQuantidadeDisponivel(),
                livro.getDescricao()
        );
    }

    public Livro toDomain(LivroJpaEntity entity) {
        return Livro.reconstituir(
                entity.getId(),
                entity.getTitulo(),
                entity.getAutor(),
                Isbn.de(entity.getIsbn()),
                entity.getQuantidadeTotal(),
                entity.getQuantidadeDisponivel(),
                entity.getDescricao()
        );
    }
}
