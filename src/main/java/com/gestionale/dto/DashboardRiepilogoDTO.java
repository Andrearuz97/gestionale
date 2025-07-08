package com.gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardRiepilogoDTO {
    private long totalePrenotazioni;
    private long prenotazioniOggi;
    private double incassoTotale;
    private double incassoOggi;
    private long totaleClienti;
    private long clientiOggi;
}
