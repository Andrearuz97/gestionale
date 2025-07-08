package com.gestionale.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PrenotazioneDTO {
    private Long clienteId;             
    private String nome;                
    private String cognome;
    private String email;    
    private String telefono;
    private String dataNascita;
    private LocalDateTime dataOra;
    private Long trattamentoId;
    private String note;
    private String stato;
}
