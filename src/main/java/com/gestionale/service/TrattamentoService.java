package com.gestionale.service;

import com.gestionale.entity.Trattamento;
import com.gestionale.repository.TrattamentoRepository;
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

    public void cancella(Long id) {
        repository.deleteById(id);
    }

    public List<Trattamento> cercaPerNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }
}
