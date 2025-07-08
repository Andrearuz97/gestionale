package com.gestionale.service;

import com.gestionale.entity.Utente;
import com.gestionale.enums.Ruolo;
import com.gestionale.repository.UtenteRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UtenteRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UtenteRepository repo) {
        this.repo = repo;
    }

    public Utente register(String nome, String email, String password) {
        if (repo.existsByEmail(email)) throw new RuntimeException("Email già registrata");

        Utente u = new Utente();
        u.setNome(nome);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setRuolo(Ruolo.USER);

        return repo.save(u);
    }

    public Utente login(String email, String password) {
        Utente u = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Email non trovata"));
        if (!encoder.matches(password, u.getPassword())) throw new RuntimeException("Password errata");
        return u;
    }
}
