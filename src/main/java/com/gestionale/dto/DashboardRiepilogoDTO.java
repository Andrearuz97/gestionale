package com.gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRiepilogoDTO {
    private long totalePrenotazioni;
    private long prenotazioniPeriodo;
    private double incassoTotale;
    private double incassoPeriodo;
    private long totaleClienti;
    private long clientiPeriodo;

    private long prenotazioniCreate;
    private long prenotazioniConfermate;
    private long prenotazioniCompletate;
    private long prenotazioniAnnullate;
}
