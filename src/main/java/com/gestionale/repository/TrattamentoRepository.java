package com.gestionale.repository;

import com.gestionale.entity.Trattamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrattamentoRepository extends JpaRepository<Trattamento, Long> {
    List<Trattamento> findByNomeContainingIgnoreCase(String nome);
    List<Trattamento> findByAttivoTrue();

}
