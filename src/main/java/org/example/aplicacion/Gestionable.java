package org.example.aplicacion;
import java.util.List;

public interface Gestionable<T> {
    List<T> cargarTodos();
}