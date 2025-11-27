package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Ingrediente;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class InventarioManager {

    public List<Ingrediente> cargarIngredientes() {
        List<Ingrediente> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("ingredientes");
        for (Document doc : collection.find()) {
            try {
                int id = doc.getObjectId("_id").hashCode();
                String nombre = doc.getString("nombre");
                double stock = doc.get("stock", Number.class).doubleValue();
                String unidad = doc.getString("unidadMedida");
                String categoria = doc.getString("categoria");
                java.util.Date fechaDate = doc.getDate("caducidad");
                java.time.LocalDate fecha = (fechaDate != null) ?
                        fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                Ingrediente ing = new Ingrediente(id, nombre, stock, unidad, categoria, fecha);
                lista.add(ing);
            } catch (Exception e) {
                System.err.println("Error cargando ingrediente: " + doc.toJson() + " -> " + e.getMessage());
            }
        }
        return lista;
    }
    public boolean verificarStock(List<Ingrediente> ingredientesReceta, int cantidadPlatos) {
        List<Ingrediente> inventario = cargarIngredientes();
        if (inventario.isEmpty()) {
            System.out.println("No se puede acceder porque no hay inventario");
        } else {
            for (Ingrediente ingReceta : ingredientesReceta) {
                boolean encontrado = false;
                for (Ingrediente ingInventario : inventario) {
                    if (ingInventario.getNombre().equalsIgnoreCase(ingReceta.getNombre())) {
                        double cantidadNecesaria = ingReceta.getStock() * cantidadPlatos;
                        if (ingInventario.getStock() < cantidadNecesaria) {
                            return false;
                        }
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) return false;
            }

        }
        return true;
    }
        public void descontarStock (List < Ingrediente > ingredientesReceta,int cantidadPlatos){
            List<Ingrediente> inventario = cargarIngredientes();
            for (Ingrediente ingReceta : ingredientesReceta) {
                for (Ingrediente ingInventario : inventario) {
                    if (ingInventario.getNombre().equalsIgnoreCase(ingReceta.getNombre())) {
                        double cantidadADescontar = ingReceta.getStock() * cantidadPlatos;
                        ingInventario.setStock(ingInventario.getStock() - cantidadADescontar);
                        //ingredienteRepository.actualizar(ingInventario);
                        break;
                    }
                }
            }
        }
    }
