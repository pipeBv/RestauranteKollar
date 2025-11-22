package org.example.entidades;

public class Entidad {
    private int id;
    private String nombre;

    public Entidad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
}
