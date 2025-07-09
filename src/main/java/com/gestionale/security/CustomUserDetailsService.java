package com.gestionale.security;

import com.gestionale.entity.Utente;
import com.gestionale.repository.UtenteRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    public CustomUserDetailsService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        if (!utente.isAttivo()) {
            throw new UsernameNotFoundException("Utente disabilitato");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(utente.getEmail())
                .password(utente.getPassword())
                .roles(utente.getRuolo().name())
                .build();
    }
}
