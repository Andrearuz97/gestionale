package com.gestionale.repository;

import com.gestionale.entity.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
}
