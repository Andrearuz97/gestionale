package com.gestionale.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PrenotazioneDTO {
    private String nome;
    private String telefono;
    private LocalDateTime dataOra;
    private Long trattamentoId;
    private String note;
}
