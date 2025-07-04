package com.gestionale.service;

import com.gestionale.dto.DashboardRiepilogoDTO;
import com.gestionale.dto.TrattamentoStatDTO;
import com.gestionale.entity.Prenotazione;
import com.gestionale.entity.Trattamento;
import com.gestionale.repository.PrenotazioneRepository;
import com.gestionale.repository.TrattamentoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final TrattamentoRepository trattamentoRepository;

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository, TrattamentoRepository trattamentoRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.trattamentoRepository = trattamentoRepository;
    }

    public Prenotazione salva(Prenotazione p) {
        return prenotazioneRepository.save(p);
    }

    public List<Prenotazione> getAll() {
        return prenotazioneRepository.findAll();
    }

    public Prenotazione getById(Long id) {
        return prenotazioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
    }

    public void cancella(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    public Prenotazione aggiorna(Long id, Prenotazione nuova) {
        Prenotazione esistente = getById(id);

        esistente.setNome(nuova.getNome());
        esistente.setDataOra(nuova.getDataOra());
        esistente.setNote(nuova.getNote());
        esistente.setTelefono(nuova.getTelefono());
        esistente.setStato(nuova.getStato());

        if (nuova.getTrattamento() != null && nuova.getTrattamento().getId() != null) {
            Trattamento trattamento = trattamentoRepository.findById(nuova.getTrattamento().getId())
                    .orElseThrow(() -> new RuntimeException("Trattamento non trovato"));
            esistente.setTrattamento(trattamento);
        }

        return prenotazioneRepository.save(esistente);
    }

    public List<Prenotazione> cercaPerNome(String nome) {
        return prenotazioneRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Prenotazione> cercaPerData(LocalDate giorno) {
        return prenotazioneRepository.findByDataOraBetween(
                giorno.atStartOfDay(),
                giorno.plusDays(1).atStartOfDay()
        );
    }

    public List<Prenotazione> getStorico() {
        return prenotazioneRepository.findByDataOraBefore(LocalDate.now().atStartOfDay());
    }

    public long countPrenotazioni() {
        return prenotazioneRepository.count();
    }

    // 🔥 METODO 1 - Calcola incasso totale sommando i prezzi dei trattamenti
    public double getTotaleIncassi() {
        return prenotazioneRepository.findAll().stream()
        		.mapToDouble(p -> {
        		    Double prezzo = p.getTrattamento().getPrezzo();
        		    return prezzo != null ? prezzo : 0.0;
        		})
                .sum();
    }

    // 🔥 METODO 2 - Conta le prenotazioni per trattamento
    public List<TrattamentoStatDTO> getPrenotazioniPerTrattamento() {
        return prenotazioneRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getTrattamento().getNome(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new TrattamentoStatDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    // 🔥 METODO 3 - Riepilogo completo per la dashboard
    public DashboardRiepilogoDTO getRiepilogoDashboard() {
        long totalePrenotazioni = countPrenotazioni();

        LocalDate oggi = LocalDate.now();
        long prenotazioniOggi = prenotazioneRepository.findByDataOraBetween(
                oggi.atStartOfDay(), oggi.plusDays(1).atStartOfDay()
        ).size();

        double incassoTotale = getTotaleIncassi();

        double incassoOggi = prenotazioneRepository.findByDataOraBetween(
                oggi.atStartOfDay(), oggi.plusDays(1).atStartOfDay()
            ).stream()
            .mapToDouble(p -> {
                Double prezzo = p.getTrattamento().getPrezzo();
                return prezzo != null ? prezzo : 0.0;
            }).sum();
        return new DashboardRiepilogoDTO(totalePrenotazioni, prenotazioniOggi, incassoTotale, incassoOggi);
    }
}
