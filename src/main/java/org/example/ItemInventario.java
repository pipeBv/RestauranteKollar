package org.example;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class ItemInventario {

    @BsonId
    private ObjectId id;
    private String nombre;
    private int stock;

    public ItemInventario() {
    }

    public ItemInventario(String nombre, int stock) {
        this.id = new ObjectId();
        this.nombre = nombre;
        this.stock = stock;
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

    public int getStock() {
        return stock;
    }
}
