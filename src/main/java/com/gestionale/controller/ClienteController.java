package com.gestionale.controller;

import com.gestionale.dto.ClienteDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.service.ClienteService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clienti")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClienteDTO crea(@RequestBody ClienteDTO dto) {
        Cliente cliente = service.salva(dto);
        return service.toDTO(cliente);
    }

    // ✅ Filtro intelligente come nelle prenotazioni
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClienteDTO> getAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String nomeCompleto) {

        List<Cliente> clienti;

        if (nomeCompleto != null && !nomeCompleto.isBlank()) {
            clienti = service.cercaPerNomeOCognome(nomeCompleto);
        } else if (nome != null && cognome != null) {
            clienti = service.cercaPerNomeOCognome(nome + " " + cognome);
        } else if (nome != null) {
            clienti = service.cercaPerNomeOCognome(nome);
        } else if (cognome != null) {
            clienti = service.cercaPerNomeOCognome(cognome);
        } else {
            clienti = service.getAll();
        }

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
    public ClienteDTO aggiorna(@PathVariable Long id, @RequestBody ClienteDTO aggiornato) {
        Cliente cliente = service.aggiorna(id, aggiornato);
        return service.toDTO(cliente);
    }
}
