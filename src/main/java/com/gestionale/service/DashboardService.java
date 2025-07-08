package com.gestionale.service;

import com.gestionale.dto.DashboardRiepilogoDTO;
import com.gestionale.dto.TrattamentoStatDTO;
import com.gestionale.entity.Prenotazione;
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

        long totalePrenotazioni = prenotazioneRepository.count();
        long prenotazioniPeriodo = prenotazioneRepository.findByDataPrenotazioneBetween(inizioPeriodo, finePeriodo).size();

        double incassoTotale = calcolaIncasso(prenotazioneRepository.findAll());
        double incassoPeriodo = calcolaIncasso(prenotazioneRepository.findByDataPrenotazioneBetween(inizioPeriodo, finePeriodo));

        long totaleClienti = clienteRepository.count();
        long clientiPeriodo = clienteRepository.countByDataRegistrazioneBetween(inizioPeriodo, finePeriodo);

        System.out.println("👥 Clienti registrati nel periodo: " + clientiPeriodo);
        System.out.println("🕓 Filtro periodo: " + periodo);
        System.out.println("📆 Inizio periodo: " + inizioPeriodo);
        System.out.println("📆 Fine periodo:   " + finePeriodo);

        return new DashboardRiepilogoDTO(
                totalePrenotazioni,
                prenotazioniPeriodo,
                incassoTotale,
                incassoPeriodo,
                totaleClienti,
                clientiPeriodo // <-- questo ora sarà clientiOggi nel DTO
        );
    }

    private LocalDateTime getInizioPeriodo(String periodo) {
        LocalDate oggi = LocalDate.now();
        return switch (periodo.toLowerCase()) {
            case "7giorni" -> oggi.minusDays(7).atStartOfDay();
            case "mese"    -> oggi.minusMonths(1).atStartOfDay();
            case "anno"    -> oggi.minusYears(1).atStartOfDay();
            case "tutto"   -> LocalDate.of(2000, 1, 1).atStartOfDay();
            default        -> oggi.atStartOfDay(); // "oggi"
        };
    }

    private double calcolaIncasso(List<Prenotazione> prenotazioni) {
        return prenotazioni.stream()
                .mapToDouble(p -> {
                    if (p.getTrattamento() != null && p.getTrattamento().getPrezzo() != null) {
                        return p.getTrattamento().getPrezzo();
                    }
                    return 0.0;
                }).sum();
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
