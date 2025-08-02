package com.gestionale.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProfiloUtenteDTO {
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private LocalDate dataNascita;
    private String ruolo;

    private List<PrenotazioneDTO> storicoPrenotazioni;
}
