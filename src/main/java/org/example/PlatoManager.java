package org.example;

import org.bson.types.ObjectId;
import java.util.*;

public class PlatoManager {
    private ArrayList<Plato> listaPlatos;
    public PlatoManager() {
        this.listaPlatos = new ArrayList<>();
    }

    public void agregarPlato(String nombre, double precio) {
        Plato nuevoPlato = new Plato(nombre, precio, new ArrayList<>());
        listaPlatos.add(nuevoPlato);
    }

    public ArrayList<Plato> obtenerPlatos() {
        return listaPlatos;
    }

    public boolean eliminarPlato(String idTexto) {
        try {
            ObjectId idBuscado = new ObjectId(idTexto);
            return listaPlatos.removeIf(plato -> plato.getId().equals(idBuscado));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}