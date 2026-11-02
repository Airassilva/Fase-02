package dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence;

import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "emprestimo")
@Getter
@NoArgsConstructor
public class EmprestimoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "livro_id", nullable = false)
    private UUID livroId;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEmprestimo status;

    @Version
    @Column(name = "versao", nullable = false)
    private long versao;

    EmprestimoJpaEntity(
            UUID id,
            UUID usuarioId,
            UUID livroId,
            LocalDate dataEmprestimo,
            LocalDate dataPrevistaDevolucao,
            LocalDate dataDevolucao,
            StatusEmprestimo status
    ) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
    }

    void atualizarDe(EmprestimoJpaEntity origem) {
        this.dataDevolucao = origem.dataDevolucao;
        this.status = origem.status;
    }
}
