package dev.aira.bibliotecanucleo.emprestimo.application.service;

import dev.aira.bibliotecanucleo.emprestimo.application.model.TipoSolicitacaoLivro;
import dev.aira.bibliotecanucleo.emprestimo.application.model.EmprestimoEvento;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.LimiteEmprestimosAtingidoException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.ReservaDisponivelParaOutroUsuarioException;
import dev.aira.bibliotecanucleo.emprestimo.application.exception.ReservaJaAbertaException;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.EmprestarLivroUseCase.CadastrarEmprestimoCommand;
import dev.aira.bibliotecanucleo.emprestimo.application.port.in.ReservarLivroUseCase.ReservarLivroCommand;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoRepositoryPort;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EventoEmprestimoPublisherPort;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.LivroGateway;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.UsuarioGateway;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Emprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.PoliticaPrazo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.Reserva;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusEmprestimo;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.StatusReserva;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoUnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmprestimoServiceTest {
    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID LIVRO_ID = UUID.randomUUID();
    private static final Instant AGORA = Instant.parse("2026-11-02T18:00:00Z");
    private static final Clock CLOCK = Clock.fixed(AGORA, ZoneId.of("America/Recife"));

    private EmprestimoRepositoryPort repository;
    private EventoEmprestimoPublisherPort publisherPort;
    private LivroGateway livroGateway;
    private UsuarioGateway usuarioGateway;
    private EmprestimoService service;

    @BeforeEach
    void setUp() {
        repository = mock(EmprestimoRepositoryPort.class);
        publisherPort = mock(EventoEmprestimoPublisherPort.class);
        livroGateway = mock(LivroGateway.class);
        usuarioGateway = mock(UsuarioGateway.class);
        EmprestimoUnitOfWork unitOfWork = new EmprestimoUnitOfWork() {
            @Override
            public <T> T executar(java.util.function.Supplier<T> operacao) {
                return operacao.get();
            }
        };
        PoliticaPrazo politicaPrazo = LocalDate::plus;
        service = new EmprestimoService(
                repository,
                publisherPort,
                livroGateway,
                usuarioGateway,
                politicaPrazo,
                unitOfWork,
                CLOCK
        );

        var usuario = new UsuarioGateway.UsuarioInfo(
                USUARIO_ID, "Aira Silva", java.time.Period.ofDays(14), 3, true);
        when(usuarioGateway.buscarUsuario(USUARIO_ID)).thenReturn(usuario);
        when(usuarioGateway.buscarUsuarioParaEmprestimo(USUARIO_ID)).thenReturn(usuario);
    }

    @Test
    void rotaDeEmprestimoComLivroDisponivelDeveCriarEmprestimo() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(true);
        when(repository.salvarEmprestimo(any(Emprestimo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.emprestarLivro(new CadastrarEmprestimoCommand(LIVRO_ID, USUARIO_ID));

        assertEquals(TipoSolicitacaoLivro.EMPRESTIMO, resultado.tipo());
        assertEquals("O Hobbit", resultado.emprestimo().nomeLivro());
        assertEquals("Aira Silva", resultado.emprestimo().nomeUsuario());
        assertEquals(LocalDate.of(2026, 11, 2), resultado.emprestimo().dataEmprestimo());
        assertEquals(LocalDate.of(2026, 11, 16), resultado.emprestimo().dataPrevistaDevolucao());
        verify(publisherPort).publicar(argThat(evento ->
                evento.tipo() == EmprestimoEvento.TipoEvento.EMPRESTIMO_REALIZADO));
        verify(repository, never()).salvarReserva(any(Reserva.class));
    }

    @Test
    void rotaDeReservaComLivroDisponivelDeveCriarEmprestimo() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(true);
        when(repository.salvarEmprestimo(any(Emprestimo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.reservarLivro(new ReservarLivroCommand(LIVRO_ID, USUARIO_ID));

        assertEquals(TipoSolicitacaoLivro.EMPRESTIMO, resultado.tipo());
        verify(repository).salvarEmprestimo(any(Emprestimo.class));
        verify(repository, never()).salvarReserva(any(Reserva.class));
    }

    @Test
    void rotaDeEmprestimoSemExemplarDeveCriarReserva() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(false);
        when(repository.salvarReserva(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.emprestarLivro(new CadastrarEmprestimoCommand(LIVRO_ID, USUARIO_ID));

        assertEquals(TipoSolicitacaoLivro.RESERVA, resultado.tipo());
        assertEquals(AGORA, resultado.reserva().dataReserva());
        verify(livroGateway).retirarExemplar(LIVRO_ID);
        verify(repository, never()).salvarEmprestimo(any(Emprestimo.class));
        verify(publisherPort, never()).publicar(any(EmprestimoEvento.class));
    }

    @Test
    void rotaDeReservaSemExemplarDeveCriarReserva() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(false);
        when(repository.salvarReserva(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.reservarLivro(new ReservarLivroCommand(LIVRO_ID, USUARIO_ID));

        assertEquals(TipoSolicitacaoLivro.RESERVA, resultado.tipo());
        verify(repository).salvarReserva(any(Reserva.class));
        verify(repository, never()).salvarEmprestimo(any(Emprestimo.class));
        verify(publisherPort, never()).publicar(any(EmprestimoEvento.class));
    }

    @Test
    void usuarioDaReservaDisponivelDeveReceberEmprestimoEConcluirReserva() {
        givenLivro();
        var reserva = Reserva.reconstituir(
                UUID.randomUUID(),
                USUARIO_ID,
                LIVRO_ID,
                Instant.parse("2026-10-25T10:00:00Z"),
                StatusReserva.DISPONIVEL
        );
        when(repository.buscarReservaDisponivel(LIVRO_ID)).thenReturn(Optional.of(reserva));
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(true);
        when(repository.salvarEmprestimo(any(Emprestimo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.atualizarReserva(reserva)).thenReturn(reserva);

        var resultado = service.emprestarLivro(new CadastrarEmprestimoCommand(LIVRO_ID, USUARIO_ID));

        assertEquals(TipoSolicitacaoLivro.EMPRESTIMO, resultado.tipo());
        assertEquals(StatusReserva.ATENDIDA, reserva.getStatus());
        verify(repository).atualizarReserva(reserva);
        verify(publisherPort).publicar(argThat(evento ->
                evento.tipo() == EmprestimoEvento.TipoEvento.EMPRESTIMO_REALIZADO));
    }

    @Test
    void outroUsuarioNaoPodeRetirarExemplarDisponivelParaReserva() {
        givenLivro();
        var outroUsuarioId = UUID.randomUUID();
        var reserva = Reserva.reconstituir(
                UUID.randomUUID(),
                outroUsuarioId,
                LIVRO_ID,
                Instant.parse("2026-10-25T10:00:00Z"),
                StatusReserva.DISPONIVEL
        );
        when(repository.buscarReservaDisponivel(LIVRO_ID)).thenReturn(Optional.of(reserva));

        var comando = new CadastrarEmprestimoCommand(LIVRO_ID, USUARIO_ID);
        assertThrows(
                ReservaDisponivelParaOutroUsuarioException.class,
                () -> service.emprestarLivro(comando)
        );

        verify(livroGateway).retirarExemplar(LIVRO_ID);
        verify(repository, never()).salvarEmprestimo(any(Emprestimo.class));
    }

    @Test
    void disponibilidadeEsgotadaEntreConsultaERetiradaDeveCriarReserva() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(false);
        when(repository.salvarReserva(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.emprestarLivro(new CadastrarEmprestimoCommand(LIVRO_ID, USUARIO_ID));

        assertEquals(TipoSolicitacaoLivro.RESERVA, resultado.tipo());
        verify(repository, never()).salvarEmprestimo(any(Emprestimo.class));
        verify(repository).salvarReserva(any(Reserva.class));
    }

    @Test
    void deveRecusarUmaSegundaReservaAbertaDoMesmoUsuarioELivro() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(false);
        when(repository.existeReservaAberta(USUARIO_ID, LIVRO_ID)).thenReturn(true);

        var comando = new ReservarLivroCommand(LIVRO_ID, USUARIO_ID);
        assertThrows(
                ReservaJaAbertaException.class,
                () -> service.reservarLivro(comando)
        );
        verify(repository, never()).salvarReserva(any(Reserva.class));
    }

    @Test
    void deveImpedirEmprestimoQuandoUsuarioAtingiuOLimite() {
        givenLivro();
        when(livroGateway.retirarExemplar(LIVRO_ID)).thenReturn(true);
        when(repository.contarEmprestimosAtivos(USUARIO_ID)).thenReturn(3L);

        var comando = new CadastrarEmprestimoCommand(LIVRO_ID, USUARIO_ID);
        assertThrows(
                LimiteEmprestimosAtingidoException.class,
                () -> service.emprestarLivro(comando)
        );
        verify(livroGateway).retirarExemplar(LIVRO_ID);
        verify(repository, never()).salvarEmprestimo(any(Emprestimo.class));
    }

    @Test
    void devolucaoAtualizaEmprestimoEstoqueEProximaReserva() {
        UUID emprestimoId = UUID.randomUUID();
        var emprestimo = Emprestimo.reconstituir(
                emprestimoId,
                USUARIO_ID,
                LIVRO_ID,
                LocalDate.of(2026, 10, 20),
                LocalDate.of(2026, 11, 3),
                null,
                StatusEmprestimo.ATIVO
        );
        var reserva = Reserva.registrar(
                UUID.randomUUID(),
                LIVRO_ID,
                Instant.parse("2026-10-25T10:00:00Z")
        );
        when(repository.buscarEmprestimo(emprestimoId)).thenReturn(Optional.of(emprestimo));
        when(repository.atualizarEmprestimo(emprestimo)).thenReturn(emprestimo);
        when(repository.buscarProximaReservaAtiva(LIVRO_ID)).thenReturn(Optional.of(reserva));
        when(repository.atualizarReserva(reserva)).thenReturn(reserva);
        when(livroGateway.buscarLivro(LIVRO_ID))
                .thenReturn(new LivroGateway.LivroInfo(
                        LIVRO_ID, "O Hobbit", "J. R. R. Tolkien", "9780306406157"));

        var detalhe = service.devolverLivro(emprestimoId, USUARIO_ID);

        assertEquals(StatusEmprestimo.DEVOLVIDO, detalhe.status());
        assertEquals(LocalDate.of(2026, 11, 2), emprestimo.getDataDevolucao());
        assertEquals(StatusReserva.DISPONIVEL, reserva.getStatus());
        verify(repository).atualizarEmprestimo(emprestimo);
        verify(livroGateway).devolverExemplar(LIVRO_ID);
        verify(repository).atualizarReserva(reserva);
    }

    @Test
    void devolucaoRejeitaEmprestimoVinculadoAOutroUsuario() {
        UUID emprestimoId = UUID.randomUUID();
        var emprestimo = Emprestimo.reconstituir(
                emprestimoId,
                USUARIO_ID,
                LIVRO_ID,
                LocalDate.of(2026, 10, 20),
                LocalDate.of(2026, 11, 3),
                null,
                StatusEmprestimo.ATIVO
        );
        when(repository.buscarEmprestimo(emprestimoId)).thenReturn(Optional.of(emprestimo));

        UUID outroUsuarioId = UUID.randomUUID();
        assertThrows(
                dev.aira.bibliotecanucleo.emprestimo.application.exception.EmprestimoNaoPertenceAoUsuarioException.class,
                () -> service.devolverLivro(emprestimoId, outroUsuarioId)
        );

        verify(repository, never()).atualizarEmprestimo(any(Emprestimo.class));
        verify(livroGateway, never()).devolverExemplar(LIVRO_ID);
    }

    private void givenLivro() {
        when(livroGateway.buscarLivro(LIVRO_ID))
                .thenReturn(new LivroGateway.LivroInfo(
                        LIVRO_ID, "O Hobbit", "J. R. R. Tolkien", "9780306406157"));
    }
}
