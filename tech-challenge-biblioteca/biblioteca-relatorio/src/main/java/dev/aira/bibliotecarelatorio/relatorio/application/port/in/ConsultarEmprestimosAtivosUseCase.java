package dev.aira.bibliotecarelatorio.relatorio.application.port.in;

import dev.aira.bibliotecarelatorio.relatorio.application.query.EmprestimoAtivoConsulta;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageQuery;
import dev.aira.bibliotecarelatorio.relatorio.application.query.PageResult;

public interface ConsultarEmprestimosAtivosUseCase {
    PageResult<EmprestimoAtivoConsulta> consultar(PageQuery pagina);
}
