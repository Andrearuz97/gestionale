package com.gestionale.service;

import com.gestionale.dto.ClienteDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Utente;
import com.gestionale.repository.ClienteRepository;
import com.gestionale.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final UtenteRepository utenteRepository; // ✅ AGGIUNTO

    // ✅ Costruttore aggiornato
    public ClienteService(ClienteRepository repository, UtenteRepository utenteRepository) {
        this.repository = repository;
        this.utenteRepository = utenteRepository;
    }

    public Cliente salva(Cliente cliente) {
        return repository.save(cliente);
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
            String[] parts = query.split("\\s+", 2);
            String nome = parts[0];
            String cognome = parts[1];
            return repository.findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCase(nome, cognome);
        } else {
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

        if (dto.getId() == null) {
            c.setDataRegistrazione(LocalDateTime.now());
        }

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

        // 🔍 Verifica se esiste un utente associato a questa email
        Optional<Utente> maybeUtente = utenteRepository.findByEmail(c.getEmail());

        boolean esisteUtente = maybeUtente.isPresent();
        boolean isAttivo = maybeUtente.map(Utente::isAttivo).orElse(false);

        dto.setGiaUtente(esisteUtente); // → usato per sapere se mostrare 🚀 o meno
        dto.setAttivo(isAttivo);        // → usato per sapere se mostrare ❌ o 🔓

        return dto;
    }

    }



    	

