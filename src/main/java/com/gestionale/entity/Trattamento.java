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

    @Column(length = 1000)
    private String descrizione;

    @Column(nullable = true)
    private Double prezzo;


    private int durata; // in minuti
    
    @Column(nullable = false)
    private boolean attivo = true;

}
