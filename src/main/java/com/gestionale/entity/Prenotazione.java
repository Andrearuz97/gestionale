package com.gestionale.entity;

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
}
