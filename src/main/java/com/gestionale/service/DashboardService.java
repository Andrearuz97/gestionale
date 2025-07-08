package com.gestionale.service;

import com.gestionale.dto.DashboardRiepilogoDTO;
import com.gestionale.dto.TrattamentoStatDTO;
import com.gestionale.entity.Prenotazione;
import com.gestionale.repository.PrenotazioneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PrenotazioneRepository prenotazioneRepository;

    // Ritorna i dati riepilogativi per la dashboard (totali e del giorno)
    public DashboardRiepilogoDTO getRiepilogoDashboard() {
        long totalePrenotazioni = prenotazioneRepository.count();
        LocalDate oggi = LocalDate.now();
        LocalDateTime inizioGiorno = oggi.atStartOfDay();
        LocalDateTime fineGiorno = oggi.plusDays(1).atStartOfDay();

        // Prenotazioni effettuate oggi (dataPrenotazione)
        long prenotazioniOggi = prenotazioneRepository
                .findByDataPrenotazioneBetween(inizioGiorno, fineGiorno)
                .size();

        // Incassi totali e del giorno (su base dataPrenotazione)
        double incassoTotale = calcolaIncasso(prenotazioneRepository.findAll());
        double incassoOggi = calcolaIncasso(
                prenotazioneRepository.findByDataPrenotazioneBetween(inizioGiorno, fineGiorno)
        );

        return new DashboardRiepilogoDTO(totalePrenotazioni, prenotazioniOggi, incassoTotale, incassoOggi);
    }

    // Calcolo dell'incasso totale da una lista di prenotazioni
    private double calcolaIncasso(List<Prenotazione> prenotazioni) {
        return prenotazioni.stream()
                .mapToDouble(p -> {
                    if (p.getTrattamento() != null && p.getTrattamento().getPrezzo() != null) {
                        return p.getTrattamento().getPrezzo();
                    }
                    return 0.0;
                }).sum();
    }

    // Statistiche: numero di prenotazioni per ogni trattamento
    public List<TrattamentoStatDTO> getPrenotazioniPerTrattamento() {
        return prenotazioneRepository.findAll().stream()
                .filter(p -> p.getTrattamento() != null && p.getTrattamento().getNome() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getTrattamento().getNome(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> new TrattamentoStatDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
