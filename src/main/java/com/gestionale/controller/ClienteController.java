package com.gestionale.controller;

import com.gestionale.dto.ClienteDTO;
import com.gestionale.dto.ClienteInputDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Utente;
import com.gestionale.enums.Ruolo;
import com.gestionale.repository.UtenteRepository;
import com.gestionale.service.ClienteService;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ClienteDTO crea(@RequestBody ClienteInputDTO inputDTO) {
        Cliente cliente = service.salva(inputDTO);
        return service.toDTO(cliente);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDTO> getAll(@RequestParam(required = false) String filtro) {
        List<Cliente> clienti = (filtro != null && !filtro.isBlank())
                ? service.ricercaSmart(filtro)
                : service.getAll();

        return clienti.stream().map(service::toDTO).collect(Collectors.toList());
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
    public ClienteDTO aggiorna(@PathVariable Long id, @RequestBody ClienteInputDTO inputDTO) {
        Cliente cliente = service.aggiorna(id, inputDTO);
        return service.toDTO(cliente);
    }

    @PostMapping("/{id}/promuovi-a-utente")
    public String promuoviAUtente(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (password == null || password.length() < 4) return "❌ Password troppo corta o mancante.";

        Cliente cliente = service.getById(id);
        if (cliente == null) return "❌ Cliente non trovato.";

        if (utenteRepository.existsByEmail(cliente.getEmail())) return "❌ Esiste già un utente con questa email.";

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

        utente.setAttivo(false);
        utente.setCliente(null);
        utenteRepository.save(utente);

        cliente.setGiaUtente(false);
        service.salvaFromEntity(cliente);

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
