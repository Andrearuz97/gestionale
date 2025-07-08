package com.gestionale.config;

import com.gestionale.entity.Utente;
import com.gestionale.enums.Ruolo;
import com.gestionale.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (utenteRepository.findByEmail("admin@esteticaluce.it").isEmpty()) {
            Utente admin = Utente.builder()
                    .nome("Super")
                    .cognome("Admin")
                    .email("admin@esteticaluce.it")
                    .password(passwordEncoder.encode("admin123"))
                    .ruolo(Ruolo.ADMIN)
                    .build();
            utenteRepository.save(admin);
            System.out.println("✅ SuperAdmin creato.");
        }
    }
}
