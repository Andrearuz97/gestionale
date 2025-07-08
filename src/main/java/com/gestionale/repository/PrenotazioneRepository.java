package com.gestionale.repository;

import com.gestionale.entity.Prenotazione;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    @EntityGraph(attributePaths = {"cliente", "trattamento"})
    List<Prenotazione> findAll();

    List<Prenotazione> findByNomeContainingIgnoreCase(String nome);

    List<Prenotazione> findByDataOraBetween(LocalDateTime inizio, LocalDateTime fine);

    boolean existsByTrattamento_IdAndDataOra(Long trattamentoId, LocalDateTime dataOra);

    List<Prenotazione> findByDataOraBefore(LocalDateTime now);
}
