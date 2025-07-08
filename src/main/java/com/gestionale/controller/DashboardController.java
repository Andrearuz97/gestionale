package com.gestionale.controller;

import com.gestionale.dto.DashboardRiepilogoDTO;
import com.gestionale.dto.TrattamentoStatDTO;
import com.gestionale.service.DashboardService;
import com.gestionale.service.PrenotazioneService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/totale-prenotazioni")
    public long getTotalePrenotazioni() {
        return dashboardService.getRiepilogoDashboard().getTotalePrenotazioni();
    }

    @GetMapping("/totale-incassi")
    public double getTotaleIncassi() {
        return dashboardService.getRiepilogoDashboard().getIncassoTotale();
    }

    @GetMapping("/prenotazioni-per-trattamento")
    public List<TrattamentoStatDTO> getPrenotazioniPerTrattamento() {
        return dashboardService.getPrenotazioniPerTrattamento();
    }

    @GetMapping("/riepilogo")
    public DashboardRiepilogoDTO getRiepilogoDashboard() {
        return dashboardService.getRiepilogoDashboard();
    }
}
