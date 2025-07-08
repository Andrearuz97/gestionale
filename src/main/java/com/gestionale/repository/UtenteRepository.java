package com.gestionale.repository;

import com.gestionale.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    boolean existsByEmail(String email);
    Optional<Utente> findByEmail(String email);
}
