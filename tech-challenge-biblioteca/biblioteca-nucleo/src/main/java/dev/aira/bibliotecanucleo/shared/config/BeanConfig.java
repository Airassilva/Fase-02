package dev.aira.bibliotecanucleo.shared.config;

import dev.aira.bibliotecanucleo.emprestimo.adapter.in.web.EmprestimoWebMapper;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.livro.LivroGatewayAdapter;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence.EmprestimoJpaRepository;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence.EmprestimoPersistenceAdapter;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence.EmprestimoPersistenceMapper;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence.EmprestimoUnitOfWorkAdapter;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence.ReservaJpaRepository;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.persistence.ReservaPersistenceMapper;
import dev.aira.bibliotecanucleo.emprestimo.adapter.out.usuario.UsuarioGatewayAdapter;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoRepositoryPort;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.EmprestimoUnitOfWork;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.LivroGateway;
import dev.aira.bibliotecanucleo.emprestimo.application.port.out.UsuarioGateway;
import dev.aira.bibliotecanucleo.emprestimo.domain.model.PoliticaPrazo;
import dev.aira.bibliotecanucleo.livro.application.port.out.LivroRepositoryPort;
import dev.aira.bibliotecanucleo.usuario.application.port.out.UsuarioRepositoryPort;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT1H")
public class BeanConfig {
    @Bean
    LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()
        );
    }

    @Bean
    EmprestimoWebMapper emprestimoWebMapper() {
        return new EmprestimoWebMapper();
    }

    @Bean
    EmprestimoPersistenceMapper emprestimoPersistenceMapper() {
        return new EmprestimoPersistenceMapper();
    }

    @Bean
    ReservaPersistenceMapper reservaPersistenceMapper() {
        return new ReservaPersistenceMapper();
    }

    @Bean
    EmprestimoRepositoryPort emprestimoRepositoryPort(
            EmprestimoJpaRepository emprestimoJpaRepository,
            ReservaJpaRepository reservaJpaRepository,
            EmprestimoPersistenceMapper emprestimoMapper,
            ReservaPersistenceMapper reservaMapper
    ) {
        return new EmprestimoPersistenceAdapter(
                emprestimoJpaRepository,
                reservaJpaRepository,
                emprestimoMapper,
                reservaMapper
        );
    }

    @Bean
    LivroGateway livroGateway(LivroRepositoryPort livroRepository) {
        return new LivroGatewayAdapter(livroRepository);
    }

    @Bean
    UsuarioGateway usuarioGateway(UsuarioRepositoryPort usuarioRepository) {
        return new UsuarioGatewayAdapter(usuarioRepository);
    }

    @Bean
    EmprestimoUnitOfWork emprestimoUnitOfWork(PlatformTransactionManager transactionManager) {
        return new EmprestimoUnitOfWorkAdapter(transactionManager);
    }

    @Bean
    PoliticaPrazo politicaPrazo() {
        return LocalDate::plus;
    }
}
