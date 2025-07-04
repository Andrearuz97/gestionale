package com.gestionale.controller;

import com.gestionale.dto.PrenotazioneDTO;
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

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setNome(dto.getNome());
        prenotazione.setTelefono(dto.getTelefono());
        prenotazione.setDataOra(dto.getDataOra());
        prenotazione.setNote(dto.getNote());
        prenotazione.setTrattamento(trattamento);
        prenotazione.setStato(StatoPrenotazione.CREATA); // default iniziale

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
    public Prenotazione aggiorna(@PathVariable Long id, @RequestBody Prenotazione p) {
        return service.aggiorna(id, p);
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
