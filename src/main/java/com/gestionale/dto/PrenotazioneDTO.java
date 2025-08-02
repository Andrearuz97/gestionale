package com.gestionale.dto;

import java.time.LocalDateTime;

import com.gestionale.entity.Trattamento;

import lombok.Data;

@Data
public class PrenotazioneDTO {
    private Long clienteId;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private String dataNascita;
    private Long id;
    private LocalDateTime dataPrenotazione; // NUOVO CAMPO
    private LocalDateTime dataOra;
    private String nomeTrattamento;
    private Trattamento trattamento;
    private Long trattamentoId;
    private String trattamentoNome;   // <--- aggiunto
    private Integer trattamentoDurata; // <--- aggiunto
    private String note;
    private String stato;
    private String trattamentoDescrizione; // aggiungi questo nuovo campo

}

