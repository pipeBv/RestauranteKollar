package org.example.entidades;
import java.time.LocalDate;

public class Ingrediente extends Entidad{
    private double stock;
    private String unidadMedida;
    private String categoria;
    private LocalDate fechaCaducidad;

    public Ingrediente(Object id, String nombre, double stock, String unidadMedida, String categoria, LocalDate fechaCaducidad) {
        super(id, nombre);
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.categoria = categoria;
        this.fechaCaducidad = fechaCaducidad;
    }
    public double getStock() {
        return stock;
    }
    public void setStock(double stock) {
        this.stock = stock;
    }
    public String getUnidadMedida() {
        return unidadMedida;
    }
   private void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
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

    @Override
    public String toString() {
        return getNombre();
    }

}
