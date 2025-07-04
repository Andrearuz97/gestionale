package com.gestionale.repository;

import com.gestionale.entity.Trattamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrattamentoRepository extends JpaRepository<Trattamento, Long> {
}
