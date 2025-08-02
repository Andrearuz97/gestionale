package com.gestionale.controller;

import com.gestionale.dto.UtenteDTO;
import com.gestionale.entity.Utente;
import com.gestionale.repository.UtenteRepository;

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
    public ResponseEntity<UtenteDTO> getProfiloUtente() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));

        UtenteDTO dto = new UtenteDTO();
        dto.setNome(utente.getNome());
        dto.setCognome(utente.getCognome());
        dto.setEmail(utente.getEmail());
        dto.setTelefono(utente.getTelefono());
        dto.setDataNascita(utente.getDataNascita());
        dto.setRuolo(utente.getRuolo().name()); // CORRETTO

        return ResponseEntity.ok(dto);
    }
}

