package com.gestionale.controller;

import com.gestionale.dto.DashboardRiepilogoDTO;
import com.gestionale.dto.TrattamentoStatDTO;
import com.gestionale.service.PrenotazioneService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final PrenotazioneService prenotazioneService;

    public DashboardController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @GetMapping("/totale-prenotazioni")
    public long getTotalePrenotazioni() {
        return prenotazioneService.countPrenotazioni();
    }

    @GetMapping("/totale-incassi")
    public double getTotaleIncassi() {
        return prenotazioneService.getTotaleIncassi();
    }

    @GetMapping("/prenotazioni-per-trattamento")
    public List<TrattamentoStatDTO> getPrenotazioniPerTrattamento() {
        return prenotazioneService.getPrenotazioniPerTrattamento();
    }
    @GetMapping("/riepilogo")
    public DashboardRiepilogoDTO getRiepilogoDashboard() {
        return prenotazioneService.getRiepilogoDashboard();
    }

}
