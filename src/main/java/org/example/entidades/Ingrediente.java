package org.example.entidades;
import java.time.LocalDate;

public class Ingrediente extends Entidad{
    private double stock;
    private String unidadMedida;
    private String categoria;
    private LocalDate fechaCaducidad;

    public Ingrediente(int id, String nombre, double stock, String unidadMedida, String categoria, LocalDate fechaCaducidad) {
        super(id, nombre);
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.categoria = categoria;
        this.fechaCaducidad = fechaCaducidad;
    }
}
