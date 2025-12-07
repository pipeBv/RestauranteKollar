package org.example.entidades;

import java.time.LocalDate;

public class Proveedor {
    private double precioPorUnidad;
    private double precioPorKilo;
    private double precioPorMayor;
    private String nombreIngrediente;
    private String categoria;
    private LocalDate fechaCaducidad;

    public Proveedor() {
    }

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

    public void setPrecioUnidad(double precioPorUnidad) {
        this.precioPorUnidad = precioPorUnidad;
    }
    public double getPrecioUnidad() {
        return precioPorUnidad;
    }
    public double getPrecioPorKilo() {
        return precioPorKilo;
    }
    public void setPrecioPorKilo(double precioPorKilo) {
        this.precioPorKilo = precioPorKilo;
    }
    public double getPrecioPorMayor() {
        return precioPorMayor;
    }
    public void setPrecioPorMayor(double precioPorMayor) {
        this.precioPorMayor = precioPorMayor;
    }
    public String getNombreIngrediente() {
        return nombreIngrediente;
    }
    public void setNombreIngrediente(String nombreIngrediente) {
        this.nombreIngrediente = nombreIngrediente;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }
    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
}