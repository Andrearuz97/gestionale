package com.gestionale.controller;

import com.gestionale.dto.PrenotazioneDTO;
import com.gestionale.dto.ProfiloUtenteDTO;
import com.gestionale.dto.UtenteDTO;
import com.gestionale.entity.Utente;
import com.gestionale.repository.UtenteRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping("/me")
    public ResponseEntity<ProfiloUtenteDTO> getProfiloUtente() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));

        ProfiloUtenteDTO dto = new ProfiloUtenteDTO();
        dto.setNome(utente.getNome());
        dto.setCognome(utente.getCognome());
        dto.setEmail(utente.getEmail());
        dto.setTelefono(utente.getTelefono());
        dto.setDataNascita(utente.getDataNascita());
        dto.setRuolo(utente.getRuolo().name());

        // Mapping storico prenotazioni se presente cliente
        if (utente.getCliente() != null && utente.getCliente().getPrenotazioni() != null) {
            List<PrenotazioneDTO> storico = utente.getCliente().getPrenotazioni().stream().map(p -> {
                PrenotazioneDTO pdto = new PrenotazioneDTO();
                pdto.setId(p.getId());
                pdto.setDataPrenotazione(p.getDataPrenotazione());
                pdto.setDataOra(p.getDataOra());
                pdto.setNote(p.getNote());
                pdto.setStato(p.getStato().name());
                pdto.setTrattamentoId(p.getTrattamento().getId());
                pdto.setNome(p.getCliente().getNome());
                pdto.setCognome(p.getCliente().getCognome());
                pdto.setEmail(p.getCliente().getEmail());
                pdto.setTelefono(p.getCliente().getTelefono());
                pdto.setDataNascita(p.getCliente().getDataNascita().toString());
                pdto.setNomeTrattamento(p.getTrattamento().getNome());
                pdto.setTrattamento(p.getTrattamento());
                pdto.setTrattamentoId(p.getTrattamento().getId());
                pdto.setTrattamentoNome(p.getTrattamento().getNome());
                pdto.setTrattamentoDurata(p.getTrattamento().getDurata());
                pdto.setTrattamentoDescrizione(p.getTrattamento().getDescrizione()); // nuovo



                return pdto;
            }).toList();

            dto.setStoricoPrenotazioni(storico);
        }

        return ResponseEntity.ok(dto);
    }

}

