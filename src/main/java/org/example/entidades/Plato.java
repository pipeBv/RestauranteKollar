package org.example.entidades;
import java.util.ArrayList;

public class Plato extends Entidad{
private ArrayList<Ingrediente> listaIngredientes;
private double precio;
private String descripcion;

    public Plato(int id, String nombre, ArrayList<Ingrediente> listaIngredientes, double precio, String descripcion) {
        super(id, nombre);
        this.listaIngredientes = listaIngredientes;
        this.precio = precio;
        this.descripcion = descripcion;
    }
}