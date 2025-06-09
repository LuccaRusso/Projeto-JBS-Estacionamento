package com.example.jbsestacionamento;

import com.google.type.DateTime;

import java.time.LocalDateTime;

public class Veiculo {

    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime  saida;

    public Veiculo(String placa, LocalDateTime  entrada, LocalDateTime  saida) {
        this.placa = placa;
        this.entrada = entrada;
        this.saida = saida;
    }

    public Veiculo() {

    }

    public String getPlaca() {
        return placa;
    }

    public LocalDateTime  getEntrada() {
        return entrada;
    }

    public LocalDateTime  getSaida() {
        return saida;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }
}
