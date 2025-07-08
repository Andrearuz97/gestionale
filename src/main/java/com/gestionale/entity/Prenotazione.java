package com.gestionale.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionale.enums.StatoPrenotazione;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prenotazioni")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String telefono;

    @ManyToOne
    @JoinColumn(name = "trattamento_id", nullable = false)
    private Trattamento trattamento;

    private LocalDateTime dataOra;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatoPrenotazione stato;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"prenotazioni", "utente"}) // ✅ evita loop e permette serializzazione
    private Cliente cliente;

}
