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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "trattamento_id", nullable = false)
    private Trattamento trattamento;

    @Column(name = "data_ora", nullable = false)
    private LocalDateTime dataOra;

    @Column(length = 500)
    private String note;
}
