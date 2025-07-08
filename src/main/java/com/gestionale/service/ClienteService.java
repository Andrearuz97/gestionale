package com.gestionale.service;

import com.gestionale.entity.Cliente;
import com.gestionale.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente salva(Cliente c) {
        return repository.save(c);
    }

    public List<Cliente> getAll() {
        return repository.findAll();
    }

    public Cliente getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Cliente non trovato"));
    }

    public void cancella(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
