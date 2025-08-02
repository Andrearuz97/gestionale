package com.gestionale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    private LocalDate dataNascita; 
}