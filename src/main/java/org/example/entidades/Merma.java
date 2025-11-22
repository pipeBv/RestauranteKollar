package org.example.entidades;

public class Merma extends Entidad{
   private int cantidad;
    public Merma(int id, String nombre, int cantidad) {
        super(id, nombre);
        this.cantidad = cantidad;
    }
}
