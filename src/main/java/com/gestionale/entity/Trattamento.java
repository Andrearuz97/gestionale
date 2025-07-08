package com.gestionale.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trattamenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trattamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descrizione;

    private double prezzo;

    private int durata; // in minuti
    
    @Column(nullable = false)
    private boolean attivo = true;

}
