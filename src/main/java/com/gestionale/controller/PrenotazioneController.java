package com.gestionale.controller;

import com.gestionale.dto.PrenotazioneDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Prenotazione;
import com.gestionale.entity.Trattamento;
import com.gestionale.enums.StatoPrenotazione;
import com.gestionale.repository.TrattamentoRepository;
import com.gestionale.service.PrenotazioneService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
@CrossOrigin(origins = "*")
public class PrenotazioneController {

    private final PrenotazioneService service;
    private final TrattamentoRepository trattamentoRepository;

    public PrenotazioneController(PrenotazioneService service, TrattamentoRepository trattamentoRepository) {
        this.service = service;
        this.trattamentoRepository = trattamentoRepository;
    }

    @PostMapping
    public Prenotazione creaPrenotazione(@RequestBody PrenotazioneDTO dto) {
        Trattamento trattamento = trattamentoRepository.findById(dto.getTrattamentoId())
                .orElseThrow(() -> new RuntimeException("Trattamento non trovato"));

        Cliente cliente;

        // Se è presente clienteId, recupera cliente esistente
        if (dto.getClienteId() != null) {
            cliente = service.getClienteById(dto.getClienteId());
        } else {
            // Altrimenti crea un nuovo cliente
            cliente = new Cliente();
            cliente.setNome(dto.getNome());
            cliente.setCognome(dto.getCognome());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(dto.getEmail());  // opzionale
            cliente.setDataNascita(LocalDate.parse(dto.getDataNascita()));

            cliente = service.salvaCliente(cliente);
        }

        // Crea la prenotazione
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setDataOra(dto.getDataOra());
        prenotazione.setNote(dto.getNote());
        prenotazione.setTrattamento(trattamento);
        prenotazione.setStato(StatoPrenotazione.CREATA);
        prenotazione.setCliente(cliente); // collegamento

        return service.salva(prenotazione);
    }


    @GetMapping
    public List<Prenotazione> getPrenotazioniFiltrate(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String nomeCompleto) {

        List<Prenotazione> prenotazioni = service.getAll();

        return prenotazioni.stream()
                .filter(p -> {
                    Cliente cliente = p.getCliente();
                    if (cliente == null) return false;

                    String nomeCliente = cliente.getNome() != null ? cliente.getNome().toLowerCase() : "";
                    String cognomeCliente = cliente.getCognome() != null ? cliente.getCognome().toLowerCase() : "";
                    String fullNameCliente = (nomeCliente + " " + cognomeCliente).trim();

                    boolean matchNome = (nome == null || nome.isBlank()) || nomeCliente.contains(nome.toLowerCase());
                    boolean matchCognome = (cognome == null || cognome.isBlank()) || cognomeCliente.contains(cognome.toLowerCase());
                    boolean matchNomeCompleto = (nomeCompleto == null || nomeCompleto.isBlank()) || fullNameCliente.contains(nomeCompleto.toLowerCase());

                    boolean matchData = true;
                    if (data != null && !data.isBlank()) {
                        try {
                            LocalDate filtroData = LocalDate.parse(data);
                            matchData = p.getDataOra().toLocalDate().equals(filtroData);
                        } catch (Exception e) {
                            matchData = false;
                        }
                    }

                    // se nomeCompleto è presente, ignora nome/cognome separati
                    if (nomeCompleto != null && !nomeCompleto.isBlank()) {
                        return matchNomeCompleto && matchData;
                    } else {
                        return matchNome && matchCognome && matchData;
                    }
                })
                .toList();
    }



    @GetMapping("/{id}")
    public Prenotazione getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Prenotazione aggiornaDaDto(@PathVariable Long id, @RequestBody PrenotazioneDTO dto) {
        return service.aggiornaDaDTO(id, dto);
    }



    @DeleteMapping("/{id}")
    public void cancella(@PathVariable Long id) {
        service.cancella(id);
    }

    @GetMapping("/cerca")
    public List<Prenotazione> cercaPerNome(@RequestParam String nome) {
        return service.cercaPerNome(nome);
    }

    @GetMapping("/giorno")
    public List<Prenotazione> cercaPerData(@RequestParam String data) {
        LocalDate giorno = LocalDate.parse(data);
        return service.cercaPerData(giorno);
    }

    @GetMapping("/storico")
    public List<Prenotazione> storico() {
        return service.getStorico();
    }
}
