package com.gestionale.service;

import com.gestionale.entity.Prenotazione;
import com.gestionale.repository.PrenotazioneRepository;
import com.gestionale.dto.TrattamentoStatDTO;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository repository;

    public PrenotazioneService(PrenotazioneRepository repository) {
        this.repository = repository;
    }

    public Prenotazione salva(Prenotazione prenotazione) {
        if (repository.existsByTrattamento_IdAndDataOra(prenotazione.getTrattamento().getId(), prenotazione.getDataOra())) {
            throw new RuntimeException("Prenotazione già esistente per quel trattamento a quell'ora");
        }

        return repository.save(prenotazione);
    }

    public List<Prenotazione> getAll() {
        return repository.findAll(Sort.by("dataOra").ascending());
    }

    public Prenotazione getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Prenotazione aggiorna(Long id, Prenotazione p) {
        Prenotazione esistente = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        esistente.setNome(p.getNome());
        esistente.setTelefono(p.getTelefono());
        esistente.setTrattamento(p.getTrattamento());
        esistente.setDataOra(p.getDataOra());
        esistente.setNote(p.getNote());

        return repository.save(esistente);
    }

    public void cancella(Long id) {
        repository.deleteById(id);
    }

    public List<Prenotazione> cercaPerNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Prenotazione> cercaPerData(LocalDate giorno) {
        LocalDateTime inizio = giorno.atStartOfDay();
        LocalDateTime fine = giorno.atTime(LocalTime.MAX);
        return repository.findByDataOraBetween(inizio, fine);
    }

    public List<Prenotazione> getStorico() {
        return repository.findByDataOraBefore(LocalDateTime.now());
    }

    public long countPrenotazioni() {
        return repository.count();
    }

    public double getTotaleIncassi() {
        return repository.findAll().stream()
            .mapToDouble(p -> p.getTrattamento().getPrezzo())
            .sum();
    }

    public List<TrattamentoStatDTO> getPrenotazioniPerTrattamento() {
        Map<String, Long> conteggio = repository.findAll().stream()
            .collect(Collectors.groupingBy(
                p -> p.getTrattamento().getNome(),
                Collectors.counting()
            ));

        return conteggio.entrySet().stream()
            .map(e -> new TrattamentoStatDTO(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }
}
