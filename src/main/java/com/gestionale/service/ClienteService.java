package com.gestionale.service;

import com.gestionale.dto.ClienteDTO;
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

    public Cliente salva(ClienteDTO dto) {
        return repository.save(fromDTO(dto));
    }

    public Cliente aggiorna(Long id, ClienteDTO dto) {
        Cliente esistente = getById(id);
        esistente.setNome(dto.getNome());
        esistente.setCognome(dto.getCognome());
        esistente.setEmail(dto.getEmail());
        esistente.setTelefono(dto.getTelefono());
        esistente.setDataNascita(dto.getDataNascita());
        return repository.save(esistente);
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

    public List<Cliente> cercaPerNomeOCognome(String input) {
        String query = input.trim();

        if (query.contains(" ")) {
            // Caso: nome + cognome (es. "Andrea Ruzittu")
            String[] parts = query.split("\\s+", 2);
            String nome = parts[0];
            String cognome = parts[1];
            return repository.findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCase(nome, cognome);
        } else {
            // Caso: solo nome o solo cognome
            return repository.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(query, query);
        }
    }


    public Cliente fromDTO(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setId(dto.getId());
        c.setNome(dto.getNome());
        c.setCognome(dto.getCognome());
        c.setEmail(dto.getEmail());
        c.setTelefono(dto.getTelefono());
        c.setDataNascita(dto.getDataNascita());
        return c;
    }

    public ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setCognome(c.getCognome());
        dto.setEmail(c.getEmail());
        dto.setTelefono(c.getTelefono());
        dto.setDataNascita(c.getDataNascita());
        return dto;
    }
}
