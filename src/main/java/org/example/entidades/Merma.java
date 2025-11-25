package org.example.entidades;
import java.time.LocalDate;
import java.util.Date;

public class Merma extends Entidad{
   private int cantidad;
   private String motivo;
   private LocalDate fecha;
    public Merma(Object id, String nombre, int cantidad, String motivo, LocalDate fecha) {
        super(id, nombre);
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

}
