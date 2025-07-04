package com.gestionale.service;

import com.gestionale.entity.Prenotazione;
import com.gestionale.repository.PrenotazioneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository repository;

    public PrenotazioneService(PrenotazioneRepository repository) {
        this.repository = repository;
    }

    public Prenotazione salva(Prenotazione prenotazione) {
        return repository.save(prenotazione);
    }

    public List<Prenotazione> getAll() {
        return repository.findAll();
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
}
