package org.example.entidades;
import java.util.ArrayList;
import java.util.List;

public class Plato extends Entidad{
private List<Ingrediente> listaIngredientes;
private double precio;
private String descripcion;

    public Plato(Object id, String nombre, double precio, String descripcion, List<Ingrediente> listaIngredientes) {
        super(id, nombre);
        this.listaIngredientes = listaIngredientes;
        this.precio = precio;
        this.descripcion = descripcion;
    }
    public List<Ingrediente> getListaIngredientes() {
        return listaIngredientes;
    }

    public double getPrecio() {
        return precio;
    }
    public String getDescripcion() {
        return descripcion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}