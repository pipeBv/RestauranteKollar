package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Ingrediente;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.example.entidades.Merma;
import org.example.entidades.Proveedor;
import java.time.LocalDate;
import java.util.List;

public class InventarioManager {

    public void actualizarStock(String nombreIngrediente, double cambioStock) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("ingredientes");
        collection.updateOne(
            new Document("nombre", nombreIngrediente),
            new Document("$inc", new Document("stock", cambioStock))
        );
    }

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
                        actualizarStock(ingInventario.getNombre(), -cantidadADescontar);
                        break;
                    }
                }
            }
        }

        public void auditarVencimientos() {
            System.out.println("--- Iniciando auditoría de vencimientos ---");
            List<Ingrediente> inventario = cargarIngredientes();

            // Instanciamos los managers
            MermaManager mermaManager = new MermaManager();
            PresupuestoManager presupuestoManager = new PresupuestoManager();
            ProveedorManager proveedorManager = new ProveedorManager();

            List<Proveedor> listaPrecios = proveedorManager.cargarProveedores();

            for (Ingrediente ing : inventario) {
                // Si está vencido y tiene stock
                if (ing.getFechaCaducidad() != null &&
                        ing.getFechaCaducidad().isBefore(LocalDate.now()) &&
                        ing.getStock() > 0) {

                    System.out.println("DETECTADO VENCIDO: " + ing.getNombre());

                    // 1. CALCULAR DINERO PERDIDO (Versión Mejorada con .contains)
                    double precioCosto = 0.0;
                    boolean precioEncontrado = false;

                    for (Proveedor p : listaPrecios) {
                        // Usamos contains para que encuentre "Tomate" dentro de "Tomate Limachino"
                        if (p.getNombreIngrediente().toLowerCase().contains(ing.getNombre().toLowerCase()) ||
                                ing.getNombre().toLowerCase().contains(p.getNombreIngrediente().toLowerCase())) {

                            if (p.getPrecioPorUnidad() > 0) precioCosto = p.getPrecioPorUnidad();
                            else if (p.getPrecioPorKilo() > 0) precioCosto = p.getPrecioPorKilo();
                            else precioCosto = p.getPrecioPorMayor();

                            precioEncontrado = true;
                            break;
                        }
                    }

                    if (!precioEncontrado) {
                        System.out.println("⚠️ PRECIO NO ENCONTRADO PARA: " + ing.getNombre());
                    }

                    double dineroPerdido = ing.getStock() * precioCosto;

                    // 2. REGISTRAR LA MERMA
                    Merma nuevaMerma = new Merma(
                            ing,
                            "AUTO_" + System.currentTimeMillis(),
                            ing.getNombre(),
                            ing.getStock(),
                            "Caducidad Automática (Venció el: $" + ing.getFechaCaducidad() + ")",
                            LocalDate.now()
                    );
                    mermaManager.registrarMerma(nuevaMerma);

                    // 3. ACTUALIZAR PRESUPUESTO (Sumar a pérdidas)
                    presupuestoManager.actualizarPresupuesto(0, 0, dineroPerdido);

                    // 4. ELIMINAR STOCK
                    actualizarStock(ing.getNombre(), -ing.getStock());
                }
            }
        }
}