package com.gestionale.dto;

public class TrattamentoStatDTO {
    private String trattamento;
    private long conteggio;

    public TrattamentoStatDTO(String trattamento, long conteggio) {
        this.trattamento = trattamento;
        this.conteggio = conteggio;
    }

    public String getTrattamento() {
        return trattamento;
    }

    public long getConteggio() {
        return conteggio;
    }
}
