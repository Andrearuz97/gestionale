package com.gestionale.controller;

import com.gestionale.dto.AuthResponseDTO;
import com.gestionale.dto.LoginRequest;
import com.gestionale.dto.RegisterRequest;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Utente;
import com.gestionale.enums.Ruolo;
import com.gestionale.repository.ClienteRepository;
import com.gestionale.repository.UtenteRepository;
import com.gestionale.security.JwtUtils;
import com.gestionale.security.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UtenteRepository utenteRepository;
    private final ClienteRepository clienteRepository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(
        UtenteRepository utenteRepository,
        ClienteRepository clienteRepository,
        JwtUtils jwtUtils,
        CustomUserDetailsService userDetailsService
    ) {
        this.utenteRepository = utenteRepository;
        this.clienteRepository = clienteRepository;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody RegisterRequest request) {
        if (utenteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email già in uso");
        }

        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un cliente con questa email esiste già");
        }

        // ✅ Crea cliente
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setCognome(request.getCognome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDataNascita(LocalDate.parse(request.getDataNascita()));
        clienteRepository.save(cliente);

        // ✅ Crea utente associato al cliente
        Utente utente = new Utente();
        utente.setNome(request.getNome());
        utente.setCognome(request.getCognome());
        utente.setEmail(request.getEmail());
        utente.setPassword(passwordEncoder.encode(request.getPassword()));
        utente.setRuolo(Ruolo.USER);
        utente.setTelefono(request.getTelefono());
        utente.setDataNascita(LocalDate.parse(request.getDataNascita()));
        utente.setCliente(cliente);

        utenteRepository.save(utente);

        // 🔐 Genera token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(utente.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getTelefono(),
                utente.getDataNascita()
        );
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequest request) {
        Utente utente = utenteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email non trovata"));

        if (!utente.isAttivo()) {
            throw new RuntimeException("Accesso negato: utente disabilitato.");
        }

        if (!passwordEncoder.matches(request.getPassword(), utente.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getTelefono(),
                utente.getDataNascita()
        );
    }

    
    @PostMapping("/promuovi")
    public AuthResponseDTO promuoviCliente(@RequestParam Long clienteId, @RequestBody RegisterRequest request) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente non trovato"));

        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new RuntimeException("Il cliente non ha un'email valida");
        }

        if (utenteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("Esiste già un utente con questa email");
        }

        Utente utente = new Utente();
        utente.setNome(cliente.getNome());
        utente.setCognome(cliente.getCognome());
        utente.setEmail(cliente.getEmail());
        utente.setTelefono(cliente.getTelefono());
        utente.setDataNascita(cliente.getDataNascita());
        utente.setRuolo(Ruolo.USER);
        utente.setPassword(passwordEncoder.encode(request.getPassword()));
        utente.setCliente(cliente);

        utenteRepository.save(utente);

        UserDetails userDetails = userDetailsService.loadUserByUsername(utente.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getTelefono(),
                utente.getDataNascita()
        );
    }

}
