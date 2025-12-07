package org.example.entidades;

import java.time.LocalDate;

public class Compra {
    private String ingrediente;
    private double cantidad;
    private String tipoCompra;
    private double totalGastado;
    private LocalDate fechaCompra;

    public Compra(String ingrediente, double cantidad, String tipoCompra, double totalGastado, LocalDate fecha) {
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.tipoCompra = tipoCompra;
        this.totalGastado = totalGastado;
        this.fechaCompra = fecha;
    }

    public String getIngrediente() { return ingrediente; }
    public double getCantidad() { return cantidad; }
    public String getTipoCompra() { return tipoCompra; }
    public double getTotalGastado() { return totalGastado; }
    public LocalDate getFecha() { return fechaCompra; }
}