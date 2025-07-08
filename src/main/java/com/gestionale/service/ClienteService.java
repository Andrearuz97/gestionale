package com.gestionale.service;

import com.gestionale.dto.ClienteDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    // ✅ Salvataggio da entità Cliente
    public Cliente salva(Cliente cliente) {
        return repository.save(cliente);
    }

    // ✅ Salvataggio da DTO
    public Cliente salva(ClienteDTO dto) {
        return repository.save(fromDTO(dto));
    }

    // ✅ Aggiornamento cliente esistente
    public Cliente aggiorna(Long id, ClienteDTO dto) {
        Cliente esistente = getById(id);
        esistente.setNome(dto.getNome());
        esistente.setCognome(dto.getCognome());
        esistente.setEmail(dto.getEmail());
        esistente.setTelefono(dto.getTelefono());
        esistente.setDataNascita(dto.getDataNascita());
        return repository.save(esistente);
    }

    // ✅ Restituisce tutti i clienti
    public List<Cliente> getAll() {
        return repository.findAll();
    }

    // ✅ Cerca cliente per ID
    public Cliente getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Cliente non trovato"));
    }

    // ✅ Cancella cliente per ID
    public void cancella(Long id) {
        repository.deleteById(id);
    }

    // ✅ Controlla se esiste già per email
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    // ✅ Ricerca intelligente per nome, cognome o entrambi
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

    // ✅ Conversione da DTO a entità
    public Cliente fromDTO(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setId(dto.getId());
        c.setNome(dto.getNome());
        c.setCognome(dto.getCognome());
        c.setEmail(dto.getEmail());
        c.setTelefono(dto.getTelefono());
        c.setDataNascita(dto.getDataNascita());

        // Setta la data solo se è un nuovo cliente
        if (dto.getId() == null) {
            c.setDataRegistrazione(LocalDateTime.now());
        }

        return c;
    }




    // ✅ Conversione da entità a DTO
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
