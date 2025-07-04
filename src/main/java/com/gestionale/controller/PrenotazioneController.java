package com.gestionale.controller;

import com.gestionale.entity.Prenotazione;
import com.gestionale.service.PrenotazioneService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
@CrossOrigin(origins = "*")
public class PrenotazioneController {

    private final PrenotazioneService service;

    public PrenotazioneController(PrenotazioneService service) {
        this.service = service;
    }

    @PostMapping
    public Prenotazione creaPrenotazione(@RequestBody Prenotazione prenotazione) {
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
}
