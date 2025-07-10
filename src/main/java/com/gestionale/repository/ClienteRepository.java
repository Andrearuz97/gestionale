package com.gestionale.repository;

import com.gestionale.entity.Cliente;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    List<Cliente> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
    List<Cliente> findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCase(String nome, String cognome);
    List<Cliente> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrEmailContainingIgnoreCase(
    	    String nome, String cognome, String telefono, String email
    	);

    long countByDataRegistrazioneBetween(LocalDateTime inizio, LocalDateTime fine);

}
