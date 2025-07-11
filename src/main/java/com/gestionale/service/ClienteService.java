package com.gestionale.service;

import com.gestionale.dto.ClienteDTO;
import com.gestionale.dto.ClienteInputDTO;
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
    private final UtenteRepository utenteRepository;

    public ClienteService(ClienteRepository repository, UtenteRepository utenteRepository) {
        this.repository = repository;
        this.utenteRepository = utenteRepository;
    }

    public Cliente salva(ClienteInputDTO dto) {
        return repository.save(fromInputDTO(dto));
    }

    public Cliente aggiorna(Long id, ClienteInputDTO dto) {
        Cliente esistente = getById(id);
        esistente.setNome(dto.getNome());
        esistente.setCognome(dto.getCognome());
        esistente.setEmail(dto.getEmail());
        esistente.setTelefono(dto.getTelefono());
        esistente.setDataNascita(dto.getDataNascita());
        esistente.setNote(dto.getNote());
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
            return repository.findByNomeContainingIgnoreCaseAndCognomeContainingIgnoreCase(parts[0], parts[1]);
        } else {
            return repository.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(query, query);
        }
    }

    public Cliente fromInputDTO(ClienteInputDTO dto) {
        Cliente c = new Cliente();
        c.setId(dto.getId());
        c.setNome(dto.getNome());
        c.setCognome(dto.getCognome());
        c.setEmail(dto.getEmail());
        c.setTelefono(dto.getTelefono());
        c.setDataNascita(dto.getDataNascita());
        c.setNote(dto.getNote());

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
        dto.setNote(c.getNote());
        dto.setStoricoPrenotazioni(c.getPrenotazioni());

        Optional<Utente> maybeUtente = utenteRepository.findByEmail(c.getEmail());
        dto.setGiaUtente(maybeUtente.isPresent());
        dto.setAttivo(maybeUtente.map(Utente::isAttivo).orElse(false));

        return dto;
    }

    public List<Cliente> ricercaSmart(String filtro) {
        return repository.findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                filtro, filtro, filtro, filtro
        );
    }
    public Cliente salvaFromEntity(Cliente cliente) {
        return repository.save(cliente);
    }

}
