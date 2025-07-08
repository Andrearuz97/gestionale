package com.gestionale.repository;

import com.gestionale.entity.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    // 🔹 Prenotazioni in un range di data appuntamento
    List<Prenotazione> findByDataOraBetween(LocalDateTime start, LocalDateTime end);

    // 🔹 Prenotazioni con data appuntamento precedente a una certa data (per archivio)
    List<Prenotazione> findByDataOraBefore(LocalDateTime dataLimite);

    // 🔹 Prenotazioni con dataPrenotazione tra due date
    List<Prenotazione> findByDataPrenotazioneBetween(LocalDateTime inizio, LocalDateTime fine);

    // 🔹 Filtro per nome cliente
    List<Prenotazione> findByCliente_NomeContainingIgnoreCase(String nome);

    // 🔹 Filtro per cognome cliente
    List<Prenotazione> findByCliente_CognomeContainingIgnoreCase(String cognome);

    // 🔹 Filtro per nome completo (nome + cognome)
    @Query("SELECT p FROM Prenotazione p WHERE " +
            "LOWER(CONCAT(p.cliente.nome, ' ', p.cliente.cognome)) LIKE LOWER(CONCAT('%', :nomeCompleto, '%'))")
    List<Prenotazione> findByNomeCompleto(@Param("nomeCompleto") String nomeCompleto);

    // 🔹 Nome + data
    @Query("SELECT p FROM Prenotazione p WHERE " +
            "LOWER(p.cliente.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
            "AND FUNCTION('DATE', p.dataOra) = FUNCTION('DATE', :data)")
    List<Prenotazione> findByNomeAndData(@Param("nome") String nome, @Param("data") LocalDateTime data);

    // 🔹 Cognome + data
    @Query("SELECT p FROM Prenotazione p WHERE " +
            "LOWER(p.cliente.cognome) LIKE LOWER(CONCAT('%', :cognome, '%')) " +
            "AND FUNCTION('DATE', p.dataOra) = FUNCTION('DATE', :data)")
    List<Prenotazione> findByCognomeAndData(@Param("cognome") String cognome, @Param("data") LocalDateTime data);

    // 🔹 Nome completo + data
    @Query("SELECT p FROM Prenotazione p WHERE " +
            "LOWER(CONCAT(p.cliente.nome, ' ', p.cliente.cognome)) LIKE LOWER(CONCAT('%', :nomeCompleto, '%')) " +
            "AND FUNCTION('DATE', p.dataOra) = FUNCTION('DATE', :data)")
    List<Prenotazione> findByNomeCompletoAndData(@Param("nomeCompleto") String nomeCompleto, @Param("data") LocalDateTime data);
}
