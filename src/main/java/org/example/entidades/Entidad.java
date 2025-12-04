package org.example.entidades;
public class Entidad {
    private Object id;
    private String nombre;
    public Entidad(Object id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public Object getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
}
