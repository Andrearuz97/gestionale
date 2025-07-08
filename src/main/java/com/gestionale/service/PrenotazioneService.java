package com.gestionale.service;

import com.gestionale.dto.PrenotazioneDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Prenotazione;
import com.gestionale.entity.Trattamento;
import com.gestionale.enums.StatoPrenotazione;
import com.gestionale.repository.ClienteRepository;
import com.gestionale.repository.PrenotazioneRepository;
import com.gestionale.repository.TrattamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final TrattamentoRepository trattamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public PrenotazioneService(PrenotazioneRepository prenotazioneRepository, TrattamentoRepository trattamentoRepository) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.trattamentoRepository = trattamentoRepository;
    }

    public Prenotazione salva(Prenotazione p) {
        if (p.getDataPrenotazione() == null) {
            p.setDataPrenotazione(LocalDateTime.now());
        }
        return prenotazioneRepository.save(p);
    }


    public List<Prenotazione> getAll() {
        return prenotazioneRepository.findAll();
    }

    public Prenotazione getById(Long id) {
        return prenotazioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));
    }

    public Cliente getClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente non trovato"));
    }

    public Cliente salvaCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void cancella(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    public Prenotazione aggiorna(Long id, Prenotazione nuova) {
        Prenotazione esistente = getById(id);

        esistente.setDataOra(nuova.getDataOra());
        esistente.setNote(nuova.getNote());
        esistente.setStato(nuova.getStato());

        if (nuova.getTrattamento() != null && nuova.getTrattamento().getId() != null) {
            Trattamento trattamento = trattamentoRepository.findById(nuova.getTrattamento().getId())
                    .orElseThrow(() -> new RuntimeException("Trattamento non trovato"));
            esistente.setTrattamento(trattamento);
        }

        if (nuova.getCliente() != null && nuova.getCliente().getId() != null) {
            Cliente clienteEsistente = clienteRepository.findById(nuova.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente non trovato"));

            clienteEsistente.setNome(nuova.getCliente().getNome());
            clienteEsistente.setCognome(nuova.getCliente().getCognome());
            clienteEsistente.setTelefono(nuova.getCliente().getTelefono());
            clienteEsistente.setEmail(nuova.getCliente().getEmail());
            clienteEsistente.setDataNascita(nuova.getCliente().getDataNascita());

            clienteRepository.save(clienteEsistente);
            esistente.setCliente(clienteEsistente);
        }

        return prenotazioneRepository.save(esistente);
    }

    public Prenotazione aggiornaDaDTO(Long id, PrenotazioneDTO dto) {
        Prenotazione prenotazione = getById(id);

        prenotazione.setDataPrenotazione(dto.getDataPrenotazione() != null ? dto.getDataPrenotazione() : prenotazione.getDataPrenotazione());
        prenotazione.setDataOra(dto.getDataOra());
        prenotazione.setNote(dto.getNote());

        if (dto.getStato() != null && !dto.getStato().isEmpty()) {
            try {
                prenotazione.setStato(StatoPrenotazione.valueOf(dto.getStato()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Stato non valido: " + dto.getStato());
            }
        }

        if (dto.getTrattamentoId() != null) {
            Trattamento trattamento = trattamentoRepository.findById(dto.getTrattamentoId())
                    .orElseThrow(() -> new RuntimeException("Trattamento non trovato"));
            prenotazione.setTrattamento(trattamento);
        }

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente non trovato"));

            cliente.setNome(dto.getNome());
            cliente.setCognome(dto.getCognome());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(dto.getEmail());
            cliente.setDataNascita(LocalDate.parse(dto.getDataNascita()));

            clienteRepository.save(cliente);
            prenotazione.setCliente(cliente);
        }

        return prenotazioneRepository.save(prenotazione);
    }


    public List<Prenotazione> cercaPerNome(String nome) {
        return prenotazioneRepository.findByCliente_NomeContainingIgnoreCase(nome);
    }


    public List<Prenotazione> cercaPerData(LocalDate giorno) {
        return prenotazioneRepository.findByDataOraBetween(
                giorno.atStartOfDay(),
                giorno.plusDays(1).atStartOfDay()
        );
    }

    public List<Prenotazione> getStorico() {
        return prenotazioneRepository.findByDataOraBefore(LocalDate.now().atStartOfDay());
    }
}
