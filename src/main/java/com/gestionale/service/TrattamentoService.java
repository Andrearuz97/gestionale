package com.gestionale.service;

import com.gestionale.entity.Trattamento;
import com.gestionale.repository.TrattamentoRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrattamentoService {

    private final TrattamentoRepository repository;

    public TrattamentoService(TrattamentoRepository repository) {
        this.repository = repository;
    }

    public Trattamento salva(Trattamento t) {
        return repository.save(t);
    }

    public List<Trattamento> getAll() {
        return repository.findAll();
    }

    public Trattamento getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Trattamento non trovato"));
    }

    @Transactional
    public void cancella(Long id) {
        Trattamento trattamento = getById(id);

        long prenotazioniCollegate = repository.countPrenotazioniByTrattamentoId(id);
        if (prenotazioniCollegate > 0) {
            throw new RuntimeException("Non puoi eliminare il trattamento. Esistono ancora prenotazioni collegate.");
        }

        repository.delete(trattamento);
    }


    public List<Trattamento> cercaPerNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Trattamento> getAttivi() {
        return repository.findByAttivoTrue();
    }

    public void disattiva(Long id) {
        Trattamento t = getById(id);
        t.setAttivo(false);
        repository.save(t);
    }

    public void attiva(Long id) {
        Trattamento t = getById(id);
        t.setAttivo(true);
        repository.save(t);
    }

}
