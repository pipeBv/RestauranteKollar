package org.example.interfaz;

import java.util.List;

// Usamos <T> para que sea genérica (sirva para Platos, Empleados, etc.)
public interface Gestionable<T> {
    List<T> cargarTodos();      // Reemplaza a mostrarItems
    void agregar(T item);       // Reemplaza a agregarItem
    void eliminar(String id);   // Reemplaza a eliminarItem
    // Modificar lo dejamos opcional o específico de cada clase
}