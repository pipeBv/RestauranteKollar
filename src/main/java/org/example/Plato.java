package org.example;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Plato {

    @BsonId
    private ObjectId id;
    private String nombre;
    private double precio;
    private ArrayList<ItemInventario> ingredientes = new ArrayList<>();

    public Plato() {
    }

    public Plato(String nombre, double precio, ArrayList<ItemInventario> ingredientes) {
        this.id = new ObjectId();
        this.nombre = nombre;
        this.precio = precio;
        this.ingredientes = ingredientes;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public ArrayList<ItemInventario> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(ArrayList<ItemInventario> ingredientes) {
        this.ingredientes = ingredientes;
    }
}