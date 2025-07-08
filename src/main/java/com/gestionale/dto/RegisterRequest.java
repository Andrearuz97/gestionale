package com.gestionale.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String telefono;
    private String dataNascita; // formato: "yyyy-MM-dd"
}
