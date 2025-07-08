package com.gestionale.controller;

import com.gestionale.entity.Trattamento;
import com.gestionale.service.TrattamentoService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/trattamenti")
@CrossOrigin(origins = "*")
public class TrattamentoController {

    private final TrattamentoService service;

    public TrattamentoController(TrattamentoService service) {
        this.service = service;
    }

    @PostMapping
    public Trattamento crea(@RequestBody Trattamento t) {
        return service.salva(t);
    }

    @PutMapping("/{id}")
    public Trattamento aggiorna(@PathVariable Long id, @RequestBody Trattamento t) {
        t.setId(id);
        return service.salva(t);
    }

    @GetMapping
    public List<Trattamento> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Trattamento getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void cancella(@PathVariable Long id) {
        service.cancella(id);
    }

    @GetMapping("/cerca")
    public List<Trattamento> cercaPerNome(@RequestParam String nome) {
        return service.cercaPerNome(nome);
    }
    
    @GetMapping("/attivi")
    public List<Trattamento> getAttivi() {
        return service.getAttivi();
    }

    @PatchMapping("/{id}/disattiva")
    public void disattiva(@PathVariable Long id) {
        service.disattiva(id);
    }

    @PatchMapping("/{id}/attiva")
    public void attiva(@PathVariable Long id) {
        service.attiva(id);
    }

}
