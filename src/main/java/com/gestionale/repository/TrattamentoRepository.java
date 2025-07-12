package com.gestionale.repository;

import com.gestionale.entity.Trattamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrattamentoRepository extends JpaRepository<Trattamento, Long> {
    List<Trattamento> findByNomeContainingIgnoreCase(String nome);
    List<Trattamento> findByAttivoTrue();
    @Query("SELECT COUNT(p) FROM Prenotazione p WHERE p.trattamento.id = :id")
    long countPrenotazioniByTrattamentoId(@Param("id") Long id);


}
