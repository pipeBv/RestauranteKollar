package org.example.entidades;

public class Proveedor {
    private double precioPorUnidad;
    private double precioPorKilo;
    private String tipoIngrediente;
    public Proveedor(double precioPorUnidad, double precioPorKilo, String tipoIngrediente) {
        this.precioPorUnidad = precioPorUnidad;
        this.precioPorKilo = precioPorKilo;
        this.tipoIngrediente = tipoIngrediente;
    }
    public double getPrecioPorUnidad() {
        return precioPorUnidad;
    }
    public double getPrecioPorKilo() {
        return precioPorKilo;
    }
    public String getTipoIngrediente() {
        return tipoIngrediente;
    }
}
