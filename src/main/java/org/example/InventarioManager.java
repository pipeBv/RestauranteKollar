package org.example;
import org.bson.types.ObjectId;
import java.util.*;

public class InventarioManager {

    private ArrayList<ItemInventario> listaItems;

    public InventarioManager() {
        this.listaItems = new ArrayList<>();
    }

    // Método modificado para lanzar excepción o devolver boolean si algo falla
    public void agregarItem(String nombre, int stock) {
        ItemInventario nuevoItem = new ItemInventario(nombre, stock);
        listaItems.add(nuevoItem);
    }

    public ArrayList<ItemInventario> obtenerItems() {
        return listaItems;
    }

    public boolean eliminarItem(String idTexto) {
        try {
            ObjectId idBuscado = new ObjectId(idTexto);
            return listaItems.removeIf(item -> item.getId().equals(idBuscado));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}