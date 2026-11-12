package dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence;

import dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence.entity.EmprestimoLeituraEntity;
import dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence.repository.EmprestimoLeituraJpaRepository;
import dev.aira.bibliotecarelatorio.relatorio.application.port.out.EmprestimoLeituraRepositoryPort;
import dev.aira.bibliotecarelatorio.relatorio.domain.EmprestimoLeitura;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class EmprestimoLeituraPersistenceAdapter implements EmprestimoLeituraRepositoryPort {

    private final EmprestimoLeituraJpaRepository repository;

    EmprestimoLeituraPersistenceAdapter(EmprestimoLeituraJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<EmprestimoLeitura> buscarPorId(UUID emprestimoId) {
        return repository.findById(emprestimoId).map(EmprestimoLeituraEntity::paraDominio);
    }

    @Override
    public void salvar(EmprestimoLeitura emprestimo) {
        repository.save(EmprestimoLeituraEntity.deDominio(emprestimo));
    }
}
