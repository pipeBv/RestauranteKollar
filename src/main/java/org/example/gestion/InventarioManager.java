package org.example.gestion;
import org.bson.types.ObjectId;
import org.example.entidades.Ingrediente;

import java.time.LocalDate;
import java.util.*;

public class InventarioManager {

    private ArrayList<Ingrediente> listaItems;

    public InventarioManager() {
        this.listaItems = new ArrayList<>();
    }

    public void agregarItem(int id,String nombre, int stock, String unidadMedida, String categoria, LocalDate fechaCaducidad) {
        Ingrediente nuevoItem = new Ingrediente(id,nombre, stock,unidadMedida,categoria,fechaCaducidad);
        listaItems.add(nuevoItem);
    }

    public ArrayList<Ingrediente> obtenerItems() {
        return listaItems;
    }

}