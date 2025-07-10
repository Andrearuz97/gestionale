package com.gestionale.controller;

import com.gestionale.dto.ClienteDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Utente;
import com.gestionale.enums.Ruolo;
import com.gestionale.repository.UtenteRepository;
import com.gestionale.service.ClienteService;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clienti")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService service;
    private final UtenteRepository utenteRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClienteController(ClienteService service, UtenteRepository utenteRepository) {
        this.service = service;
        this.utenteRepository = utenteRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDTO crea(@RequestBody ClienteDTO dto) {
        Cliente cliente = service.salva(dto);
        return service.toDTO(cliente);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDTO> getAll(@RequestParam(required = false) String filtro) {
        List<Cliente> clienti;

        // Se il filtro è presente, esegui la ricerca sui vari campi
        if (filtro != null && !filtro.isBlank()) {
            clienti = service.ricercaSmart(filtro);  // Cerca per nome, cognome, telefono, email
        } else {
            clienti = service.getAll();  // Altrimenti restituisci tutti i clienti
        }

        // Restituisci i clienti come DTO
        return clienti.stream()
                .map(service::toDTO)
                .collect(Collectors.toList());
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDTO getById(@PathVariable Long id) {
        return service.toDTO(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public void cancella(@PathVariable Long id) {
        service.cancella(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDTO aggiorna(@PathVariable Long id, @RequestBody ClienteDTO aggiornato) {
        Cliente cliente = service.aggiorna(id, aggiornato);
        return service.toDTO(cliente);
    }

    // 🚀 Nuovo endpoint per promozione cliente → utente
    @PostMapping("/{id}/promuovi-a-utente")
    public String promuoviAUtente(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.length() < 4) {
            return "❌ Password troppo corta o mancante.";
        }

        Cliente cliente = service.getById(id);
        if (cliente == null) {
            return "❌ Cliente non trovato.";
        }

        if (utenteRepository.existsByEmail(cliente.getEmail())) {
            return "❌ Esiste già un utente con questa email.";
        }

        Utente utente = new Utente();
        utente.setNome(cliente.getNome());
        utente.setCognome(cliente.getCognome());
        utente.setEmail(cliente.getEmail());
        utente.setTelefono(cliente.getTelefono());
        utente.setDataNascita(cliente.getDataNascita());
        utente.setRuolo(Ruolo.USER);
        utente.setPassword(passwordEncoder.encode(password));
        utente.setCliente(cliente);

        utenteRepository.save(utente);

        return "✅ Cliente promosso a utente!";
    }
    @PutMapping("/{id}/revoca-utente")
    public String revocaUtente(@PathVariable Long id) {
        Cliente cliente = service.getById(id);
        if (cliente == null) return "❌ Cliente non trovato.";

        Utente utente = utenteRepository.findByEmail(cliente.getEmail()).orElse(null);
        if (utente == null) return "❌ Nessun utente associato.";

        System.out.println("➡️ Prima: utente attivo = " + utente.isAttivo());

        utente.setAttivo(false);
        utente.setCliente(null);
        utenteRepository.save(utente);

        System.out.println("✅ Dopo: utente attivo = " + utente.isAttivo());

        cliente.setGiaUtente(false); // opzionale
        service.salva(cliente);

        return "✅ Utente disattivato e scollegato.";
    }
    @PutMapping("/{id}/riattiva-utente")
    public String riattivaUtente(@PathVariable Long id) {
        Cliente cliente = service.getById(id);
        if (cliente == null) return "❌ Cliente non trovato.";

        Utente utente = utenteRepository.findByEmail(cliente.getEmail()).orElse(null);
        if (utente == null) return "❌ Nessun utente associato.";

        utente.setAttivo(true);
        utente.setCliente(cliente);
        utenteRepository.save(utente);

        return "✅ Utente riattivato con successo.";
    }




}
