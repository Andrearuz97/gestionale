package com.gestionale.repository;

import com.gestionale.entity.Prenotazione;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    // Ricerca per nome cliente
    List<Prenotazione> findByNomeContainingIgnoreCase(String nome);

    // Ricerca per fascia oraria (es. tutte le prenotazioni in un giorno)
    List<Prenotazione> findByDataOraBetween(LocalDateTime inizio, LocalDateTime fine);

    // Controllo se esiste una prenotazione per uno specifico trattamento e ora
    boolean existsByTrattamento_IdAndDataOra(Long trattamentoId, LocalDateTime dataOra);

    // Storico: tutte le prenotazioni già passate
    List<Prenotazione> findByDataOraBefore(LocalDateTime now);
}
