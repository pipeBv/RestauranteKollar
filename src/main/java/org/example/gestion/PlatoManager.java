package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PlatoManager {
    public List<Plato> cargarPlatos() {
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
                        
                        Number iStockNum = ingDoc.get("stock", Number.class);
                        double iStock = (iStockNum != null) ? iStockNum.doubleValue() : 0.0;
                        
                        String iUnidad = ingDoc.getString("unidadMedida");
                        String iCategoria = ingDoc.getString("categoria");
                        
                        java.util.Date iFechaDate = ingDoc.getDate("fechaCaducidad");
                        if (iFechaDate == null) {
                            iFechaDate = ingDoc.getDate("caducidad");
                        }

                        java.time.LocalDate iFecha = (iFechaDate != null) ?
                                iFechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

                        listaIngredientes.add(new Ingrediente(0, iNombre, iStock, iUnidad, iCategoria, iFecha));
                    }
                }

                Plato plato = new Plato(id, nombre, precio, descripcion, listaIngredientes);
                lista.add(plato);
            } catch (Exception e) {
                System.err.println("Error cargando plato '" + doc.getString("nombre") + "': " + e.getMessage());
            }
        }
        return lista;
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