package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
import org.example.interfaz.Gestionable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


public class PlatoManager implements Gestionable<Plato> {

    @Override
    public List<Plato> cargarTodos() {
        return cargarPlatosLogica();
    }

    @Override
    public void agregar(Plato plato) {
        agregarPlato(plato.getNombre(), plato.getPrecio(), plato.getDescripcion());
    }

    @Override
    public void eliminar(String nombre) {
        eliminarPlato(nombre);
    }


    public List<Plato> cargarPlatosLogica() {
        List<Plato> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("platos");

        for (Document doc : collection.find()) {
            try {
                int id = doc.getObjectId("_id").hashCode();
                String nombre = doc.getString("nombre");
                double precio = doc.get("precio", Number.class).doubleValue();
                String descripcion = doc.getString("descripcion");

                List<Document> listaDocsIngredientes = (List<Document>) doc.get("listaIngredientes");
                List<Ingrediente> listaIngredientes = new ArrayList<>();

                if (listaDocsIngredientes != null) {
                    for (Document ingDoc : listaDocsIngredientes) {
                        String iNombre = ingDoc.getString("nombre");
                        Number iCantidadNum = ingDoc.get("cantidad", Number.class);
                        if (iCantidadNum == null) iCantidadNum = ingDoc.get("stock", Number.class);
                        double iCantidad = (iCantidadNum != null) ? iCantidadNum.doubleValue() : 0.0;
                        listaIngredientes.add(new Ingrediente(0, iNombre, iCantidad, "Unidad", "Receta", null));
                    }
                }
                lista.add(new Plato(id, nombre, precio, descripcion, listaIngredientes));
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return lista;
    }

    public List<Plato> cargarPlatos() {
        return cargarTodos(); // Redirige al método de la interfaz
    }

    public void agregarPlato(String nombre, double precio, String descripcion) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("platos");
        Document doc = new Document("nombre", nombre)
                .append("precio", precio)
                .append("descripcion", descripcion)
                .append("listaIngredientes", new ArrayList<>());
        collection.insertOne(doc);
    }

    public void eliminarPlato(String nombre) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("platos");
        collection.deleteOne(new Document("nombre", nombre));
    }

    public void modificarPlato(String nombreOriginal, double nuevoPrecio, String nuevaDescripcion) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("platos");
        collection.updateOne(new Document("nombre", nombreOriginal),
                new Document("$set", new Document("precio", nuevoPrecio).append("descripcion", nuevaDescripcion)));
    }
}