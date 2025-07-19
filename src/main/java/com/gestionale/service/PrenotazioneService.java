package com.gestionale.service;

import com.gestionale.dto.PrenotazioneDTO;
import com.gestionale.entity.Cliente;
import com.gestionale.entity.Prenotazione;
import com.gestionale.entity.Trattamento;
import com.gestionale.enums.StatoPrenotazione;
import com.gestionale.repository.ClienteRepository;
import com.gestionale.repository.PrenotazioneRepository;
import com.gestionale.repository.TrattamentoRepository;

import jakarta.transaction.Transactional;

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

        LocalDateTime inizio = p.getDataOra();
        int durata = p.getTrattamento().getDurata();
        LocalDateTime fine = inizio.plusMinutes(durata);

        List<Prenotazione> prenotazioniGiornata = prenotazioneRepository.findByDataOraBetween(
                inizio.toLocalDate().atStartOfDay(),
                inizio.toLocalDate().plusDays(1).atStartOfDay()
        );

        boolean sovrapposta = prenotazioniGiornata.stream().anyMatch(esistente -> {
            LocalDateTime esInizio = esistente.getDataOra();
            LocalDateTime esFine = esInizio.plusMinutes(esistente.getTrattamento().getDurata());
            return (inizio.isBefore(esFine) && fine.isAfter(esInizio));
        });

        if (sovrapposta) {
            throw new RuntimeException("Orario non disponibile. Esiste già una prenotazione in quel periodo.");
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

    @Transactional
    public void cancella(Long id) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        Cliente cliente = prenotazione.getCliente();
        if (cliente != null && cliente.getPrenotazioni() != null) {
            cliente.getPrenotazioni().removeIf(p -> p.getId().equals(id));
        }

        prenotazioneRepository.delete(prenotazione);
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

        LocalDateTime nuovoInizio = dto.getDataOra();
        int nuovaDurata = trattamentoRepository.findById(dto.getTrattamentoId())
                .orElseThrow(() -> new RuntimeException("Trattamento non trovato"))
                .getDurata();
        LocalDateTime nuovoFine = nuovoInizio.plusMinutes(nuovaDurata);

        List<Prenotazione> prenotazioniGiornata = prenotazioneRepository.findByDataOraBetween(
                nuovoInizio.toLocalDate().atStartOfDay(),
                nuovoInizio.toLocalDate().plusDays(1).atStartOfDay()
        );

        boolean sovrapposta = prenotazioniGiornata.stream()
                .filter(p -> !p.getId().equals(id))
                .anyMatch(p -> {
                    LocalDateTime inizio = p.getDataOra();
                    LocalDateTime fine = inizio.plusMinutes(p.getTrattamento().getDurata());
                    return nuovoInizio.isBefore(fine) && nuovoFine.isAfter(inizio);
                });

        if (sovrapposta) {
            throw new RuntimeException("Orario non disponibile. Esiste già una prenotazione in quel periodo.");
        }

        prenotazione.setDataOra(dto.getDataOra());
        prenotazione.setNote(dto.getNote());
        prenotazione.setDataPrenotazione(dto.getDataPrenotazione() != null ? dto.getDataPrenotazione() : prenotazione.getDataPrenotazione());

        if (dto.getStato() != null && !dto.getStato().isEmpty()) {
            prenotazione.setStato(StatoPrenotazione.valueOf(dto.getStato()));
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
    public boolean controllaDisponibilita(LocalDateTime dataOra, int durata) {
        LocalDateTime fine = dataOra.plusMinutes(durata);

        List<Prenotazione> prenotazioniGiornata = prenotazioneRepository.findByDataOraBetween(
                dataOra.toLocalDate().atStartOfDay(),
                dataOra.toLocalDate().plusDays(1).atStartOfDay()
        );

        return prenotazioniGiornata.stream().noneMatch(p -> {
            LocalDateTime esInizio = p.getDataOra();
            LocalDateTime esFine = esInizio.plusMinutes(p.getTrattamento().getDurata());
            return dataOra.isBefore(esFine) && fine.isAfter(esInizio);
        });
    }
    public boolean controllaDisponibilita(LocalDateTime dataOra, Long trattamentoId, Long prenotazioneIdEscludere) {
        int durata = trattamentoRepository.findById(trattamentoId)
                .orElseThrow(() -> new RuntimeException("Trattamento non trovato"))
                .getDurata();

        LocalDateTime fine = dataOra.plusMinutes(durata);

        List<Prenotazione> prenotazioni = prenotazioneRepository.findByDataOraBetween(
                dataOra.toLocalDate().atStartOfDay(),
                dataOra.toLocalDate().plusDays(1).atStartOfDay()
        );

        return prenotazioni.stream()
                .filter(p -> prenotazioneIdEscludere == null || !p.getId().equals(prenotazioneIdEscludere))
                .noneMatch(p -> {
                    LocalDateTime inizio = p.getDataOra();
                    LocalDateTime fineEsistente = inizio.plusMinutes(p.getTrattamento().getDurata());
                    return dataOra.isBefore(fineEsistente) && fine.isAfter(inizio);
                });
    }


}
