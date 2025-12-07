package org.example.entidades;
import java.time.LocalDate;


public class Merma extends Entidad{
   private double cantidad;
   private String motivo;
   private LocalDate fecha;
   private Ingrediente ingrediente;
    public Merma(Ingrediente ingrediente, Object id, String nombre, double cantidad, String motivo, LocalDate fecha) {
        super(id, nombre);
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
    }
    public double getCantidad() {
        return cantidad;
    }
    public String getMotivo() {
        return motivo;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public Ingrediente getIngrediente() {
        return ingrediente;
    }
}
