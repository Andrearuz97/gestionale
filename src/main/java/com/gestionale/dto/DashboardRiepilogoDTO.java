package com.gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardRiepilogoDTO {
    private long totalePrenotazioni;
    private long prenotazioniOggi;
    private double incassoTotale;
    private double incassoOggi;
}
