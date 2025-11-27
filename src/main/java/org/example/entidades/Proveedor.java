package org.example.entidades;

import java.time.LocalDate;

public class Proveedor {
    private double precioPorUnidad;
    private double precioPorKilo;
    private double precioPorMayor;
    private String nombreIngrediente;
    private String categoria;
    private LocalDate fechaCaducidad;
    public Proveedor(double precioPorUnidad, double precioPorKilo, double precioPorMayor, String nombreIngrediente, String categoria, LocalDate fechaCaducidad) {
        this.precioPorUnidad = precioPorUnidad;
        this.precioPorKilo = precioPorKilo;
        this.precioPorMayor = precioPorMayor;
        this.nombreIngrediente = nombreIngrediente;
        this.categoria = categoria;
        this.fechaCaducidad = fechaCaducidad;
    }
    public double getPrecioPorUnidad() {
        return precioPorUnidad;
    }
    public double getPrecioPorKilo() {
        return precioPorKilo;
    }
    public double getPrecioPorMayor() {
        return precioPorMayor;
    }
    public String getNombreIngrediente() {
        return nombreIngrediente;
    }
    public String getCategoria() {
        return categoria;
    }
    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }
}
