package dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence.entity;

import dev.aira.bibliotecarelatorio.relatorio.domain.EmprestimoLeitura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "emprestimo_leitura")
public class EmprestimoLeituraEntity {

    @Id
    @Column(name = "emprestimo_id", nullable = false)
    private UUID emprestimoId;

    @Column(name = "livro_id", nullable = false)
    private UUID livroId;

    @Column(name = "livro_titulo", nullable = false, length = 255)
    private String livroTitulo;

    @Column(name = "livro_autor", nullable = false, length = 150)
    private String livroAutor;

    @Column(name = "livro_isbn", nullable = false, length = 13)
    private String livroIsbn;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    protected EmprestimoLeituraEntity() {
    }

    public static EmprestimoLeituraEntity deDominio(EmprestimoLeitura emprestimo) {
        EmprestimoLeituraEntity entidade = new EmprestimoLeituraEntity();
        entidade.emprestimoId = emprestimo.getEmprestimoId();
        entidade.livroId = emprestimo.getLivroId();
        entidade.livroTitulo = emprestimo.getLivroTitulo();
        entidade.livroAutor = emprestimo.getLivroAutor();
        entidade.livroIsbn = emprestimo.getLivroIsbn();
        entidade.usuarioId = emprestimo.getUsuarioId();
        entidade.dataEmprestimo = emprestimo.getDataEmprestimo();
        entidade.dataPrevistaDevolucao = emprestimo.getDataPrevistaDevolucao();
        entidade.dataDevolucao = emprestimo.getDataDevolucao();
        return entidade;
    }

    public EmprestimoLeitura paraDominio() {
        return EmprestimoLeitura.reconstituir(
                emprestimoId,
                livroId,
                livroTitulo,
                livroAutor,
                livroIsbn,
                usuarioId,
                dataEmprestimo,
                dataPrevistaDevolucao,
                dataDevolucao);
    }
}
