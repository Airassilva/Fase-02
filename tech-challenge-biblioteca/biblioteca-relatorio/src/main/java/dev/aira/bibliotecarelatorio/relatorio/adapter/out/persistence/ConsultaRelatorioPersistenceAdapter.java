package dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence;

import dev.aira.bibliotecarelatorio.relatorio.application.port.out.ConsultaRelatorioPort;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimoAtivoConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimosPorDiaConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageQuery;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageResult;
import dev.aira.bibliotecarelatorio.relatorio.domain.LivroRanking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
class ConsultaRelatorioPersistenceAdapter implements ConsultaRelatorioPort {

    private static final String SQL_LIVROS_MAIS_EMPRESTADOS = """
            SELECT livro_id, MAX(livro_titulo) AS livro_titulo,
                   MAX(livro_autor) AS livro_autor, MAX(livro_isbn) AS livro_isbn,
                   COUNT(*) AS total
            FROM emprestimo_leitura
            %s
            GROUP BY livro_id
            ORDER BY total DESC, livro_titulo ASC
            LIMIT ?
            """;

    private final JdbcTemplate jdbcTemplate;

    ConsultaRelatorioPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LivroRanking> livrosMaisEmprestados(int limite) {
        return jdbcTemplate.query(
                SQL_LIVROS_MAIS_EMPRESTADOS.formatted(""),
                (rs, rowNum) -> mapearLivro(rs),
                limite);
    }

    @Override
    public List<LivroRanking> livrosMaisEmprestados(LocalDate inicio, LocalDate fim) {
        return jdbcTemplate.query(
                SQL_LIVROS_MAIS_EMPRESTADOS.formatted(
                        "WHERE data_emprestimo >= ? AND data_emprestimo <= ?"),
                (rs, rowNum) -> mapearLivro(rs),
                inicio,
                fim,
                20);
    }

    @Override
    public long contarDevolucoes(LocalDate inicio, LocalDate fim) {
        return contar("SELECT COUNT(*) FROM emprestimo_leitura WHERE data_devolucao BETWEEN ? AND ?", inicio, fim);
    }

    @Override
    public long contarEmprestimosEmAberto() {
        return contar("SELECT COUNT(*) FROM emprestimo_leitura WHERE data_devolucao IS NULL");
    }

    @Override
    public long contarEmprestimosAtrasados(LocalDate hoje) {
        return contar(
                "SELECT COUNT(*) FROM emprestimo_leitura "
                        + "WHERE data_devolucao IS NULL AND data_prevista_devolucao < ?",
                hoje);
    }

    @Override
    public List<EmprestimosPorDiaConsulta> contarEmprestimosPorDia(LocalDate inicio, LocalDate fim) {
        return jdbcTemplate.query(
                """
                        SELECT data_emprestimo AS dia, COUNT(*) AS total
                        FROM emprestimo_leitura
                        WHERE data_emprestimo BETWEEN ? AND ?
                        GROUP BY data_emprestimo
                        ORDER BY data_emprestimo
                        """,
                (rs, rowNum) -> new EmprestimosPorDiaConsulta(
                        rs.getObject("dia", LocalDate.class),
                        rs.getLong("total")),
                inicio,
                fim);
    }

    @Override
    public PageResult<EmprestimoAtivoConsulta> consultarEmprestimosAtivos(PageQuery pagina) {
        long total = contar("SELECT COUNT(*) FROM emprestimo_leitura WHERE data_devolucao IS NULL");
        long deslocamento = Math.multiplyExact((long) pagina.page(), pagina.size());
        List<EmprestimoAtivoConsulta> content = jdbcTemplate.query(
                """
                        SELECT emprestimo_id, livro_id, livro_titulo, livro_isbn,
                               data_emprestimo, data_prevista_devolucao
                        FROM emprestimo_leitura
                        WHERE data_devolucao IS NULL
                        ORDER BY data_prevista_devolucao ASC, emprestimo_id ASC
                        LIMIT ? OFFSET ?
                        """,
                (rs, rowNum) -> new EmprestimoAtivoConsulta(
                        rs.getObject("emprestimo_id", UUID.class),
                        rs.getObject("livro_id", UUID.class),
                        rs.getString("livro_titulo"),
                        rs.getString("livro_isbn"),
                        rs.getObject("data_emprestimo", LocalDate.class),
                        rs.getObject("data_prevista_devolucao", LocalDate.class),
                        false,
                        0),
                pagina.size(),
                deslocamento);
        return new PageResult<>(content, total, pagina.page(), pagina.size());
    }

    private long contar(String sql, Object... parametros) {
        Long total = jdbcTemplate.queryForObject(sql, Long.class, parametros);
        return total == null ? 0 : total;
    }

    private LivroRanking mapearLivro(ResultSet rs) throws SQLException {
        return new LivroRanking(
                rs.getObject("livro_id", UUID.class),
                rs.getString("livro_titulo"),
                rs.getString("livro_autor"),
                rs.getString("livro_isbn"),
                rs.getLong("total"));
    }
}
