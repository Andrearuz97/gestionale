package com.gestionale.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteDTO {
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private LocalDate dataNascita;
}
