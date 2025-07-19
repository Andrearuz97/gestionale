package com.gestionale.service;

import com.gestionale.dto.DashboardRiepilogoDTO;
import com.gestionale.dto.TrattamentoStatDTO;
import com.gestionale.entity.Prenotazione;
import com.gestionale.enums.StatoPrenotazione;
import com.gestionale.repository.ClienteRepository;
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
    private final ClienteRepository clienteRepository;

    public DashboardRiepilogoDTO getRiepilogoDashboard(String periodo) {
        LocalDateTime inizioPeriodo = getInizioPeriodo(periodo);
        LocalDateTime finePeriodo = LocalDateTime.now();

        List<Prenotazione> tutte = prenotazioneRepository.findAll();
        List<Prenotazione> nelPeriodo = prenotazioneRepository.findByDataPrenotazioneBetween(inizioPeriodo, finePeriodo);

        // Dati globali
        long totalePrenotazioni = tutte.size();
        double incassoTotale = calcolaIncasso(
            tutte.stream().filter(p -> p.getStato() == StatoPrenotazione.COMPLETATA).toList()
        );
        long totaleClienti = clienteRepository.count();

        // Dati del periodo
        long prenotazioniPeriodo = nelPeriodo.size();
        double incassoPeriodo = calcolaIncasso(
            nelPeriodo.stream().filter(p -> p.getStato() == StatoPrenotazione.COMPLETATA).toList()
        );
        long clientiPeriodo = clienteRepository.countByDataRegistrazioneBetween(inizioPeriodo, finePeriodo);

        // Conteggio stati nel periodo
        long create = countByStato(nelPeriodo, StatoPrenotazione.CREATA);
        long confermate = countByStato(nelPeriodo, StatoPrenotazione.CONFERMATA);
        long completate = countByStato(nelPeriodo, StatoPrenotazione.COMPLETATA);
        long annullate = countByStato(nelPeriodo, StatoPrenotazione.ANNULLATA);

        return new DashboardRiepilogoDTO(
                totalePrenotazioni,
                prenotazioniPeriodo,
                incassoTotale,
                incassoPeriodo,
                totaleClienti,
                clientiPeriodo,
                create,
                confermate,
                completate,
                annullate
        );
    }

    private long countByStato(List<Prenotazione> prenotazioni, StatoPrenotazione stato) {
        return prenotazioni.stream().filter(p -> p.getStato() == stato).count();
    }

    private LocalDateTime getInizioPeriodo(String periodo) {
        LocalDate oggi = LocalDate.now();
        return switch (periodo.toLowerCase()) {
            case "7giorni" -> oggi.minusDays(7).atStartOfDay();
            case "mese"    -> oggi.minusMonths(1).atStartOfDay();
            case "anno"    -> oggi.minusYears(1).atStartOfDay();
            case "tutto"   -> LocalDate.of(2000, 1, 1).atStartOfDay();
            default        -> oggi.atStartOfDay(); // oggi
        };
    }

    private double calcolaIncasso(List<Prenotazione> prenotazioni) {
        return prenotazioni.stream()
                .mapToDouble(p -> p.getTrattamento() != null && p.getTrattamento().getPrezzo() != null
                        ? p.getTrattamento().getPrezzo()
                        : 0.0)
                .sum();
    }

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
