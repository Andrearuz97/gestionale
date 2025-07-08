package com.gestionale.controller;

import com.gestionale.dto.PrenotazioneDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Prenotazione;
import com.gestionale.entity.Trattamento;
import com.gestionale.enums.StatoPrenotazione;
import com.gestionale.repository.TrattamentoRepository;
import com.gestionale.service.PrenotazioneService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
@CrossOrigin(origins = "*")
public class PrenotazioneController {

    private final PrenotazioneService service;
    private final TrattamentoRepository trattamentoRepository;

    public PrenotazioneController(PrenotazioneService service, TrattamentoRepository trattamentoRepository) {
        this.service = service;
        this.trattamentoRepository = trattamentoRepository;
    }

    @PostMapping
    public Prenotazione creaPrenotazione(@RequestBody PrenotazioneDTO dto) {
        Trattamento trattamento = trattamentoRepository.findById(dto.getTrattamentoId())
                .orElseThrow(() -> new RuntimeException("Trattamento non trovato"));

        Cliente cliente;

        // Se è presente clienteId, recupera cliente esistente
        if (dto.getClienteId() != null) {
            cliente = service.getClienteById(dto.getClienteId());
        } else {
            // Altrimenti crea un nuovo cliente
            cliente = new Cliente();
            cliente.setNome(dto.getNome());
            cliente.setCognome(dto.getCognome());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(dto.getEmail());  // opzionale
            cliente.setDataNascita(LocalDate.parse(dto.getDataNascita()));

            cliente = service.salvaCliente(cliente);
        }

        // Crea la prenotazione
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setDataOra(dto.getDataOra());
        prenotazione.setNote(dto.getNote());
        prenotazione.setTrattamento(trattamento);
        prenotazione.setStato(StatoPrenotazione.CREATA);
        prenotazione.setCliente(cliente); // collegamento

        return service.salva(prenotazione);
    }


    @GetMapping
    public List<Prenotazione> getAllPrenotazioni() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Prenotazione getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Prenotazione aggiornaDaDto(@PathVariable Long id, @RequestBody PrenotazioneDTO dto) {
        return service.aggiornaDaDTO(id, dto);
    }



    @DeleteMapping("/{id}")
    public void cancella(@PathVariable Long id) {
        service.cancella(id);
    }

    @GetMapping("/cerca")
    public List<Prenotazione> cercaPerNome(@RequestParam String nome) {
        return service.cercaPerNome(nome);
    }

    @GetMapping("/giorno")
    public List<Prenotazione> cercaPerData(@RequestParam String data) {
        LocalDate giorno = LocalDate.parse(data);
        return service.cercaPerData(giorno);
    }

    @GetMapping("/storico")
    public List<Prenotazione> storico() {
        return service.getStorico();
    }
}
