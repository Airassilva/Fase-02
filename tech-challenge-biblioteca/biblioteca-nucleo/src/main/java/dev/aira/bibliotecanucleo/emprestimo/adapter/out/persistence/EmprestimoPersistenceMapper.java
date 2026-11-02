package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;

public class EmprestimoPersistenceMapper {
    public EmprestimoJpaEntity toEntity(Emprestimo emprestimo) {
        return new EmprestimoJpaEntity(
                emprestimo.getId(),
                emprestimo.getUsuarioId(),
                emprestimo.getLivroId(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getDataDevolucao(),
                emprestimo.getStatus()
        );
    }

    public Emprestimo toDomain(EmprestimoJpaEntity entity) {
        return Emprestimo.reconstituir(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getLivroId(),
                entity.getDataEmprestimo(),
                entity.getDataPrevistaDevolucao(),
                entity.getDataDevolucao(),
                entity.getStatus()
        );
    }
}
