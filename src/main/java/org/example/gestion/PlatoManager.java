package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
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
                List<Ingrediente> listaIngredientes = (List<Ingrediente>) doc.get("listaIngredientes");
                Plato plato = new Plato(id, nombre, precio,descripcion,listaIngredientes);
                lista.add(plato);
            } catch (Exception e) {
                System.err.println("Error cargando plato: " + e.getMessage());
            }
        }
        return lista;
    }
}