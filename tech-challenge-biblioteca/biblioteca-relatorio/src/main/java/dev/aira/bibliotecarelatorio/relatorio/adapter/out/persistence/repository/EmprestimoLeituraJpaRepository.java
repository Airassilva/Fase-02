package dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence.repository;

import dev.aira.bibliotecarelatorio.relatorio.adapter.out.persistence.entity.EmprestimoLeituraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmprestimoLeituraJpaRepository extends JpaRepository<EmprestimoLeituraEntity, UUID> {
}
