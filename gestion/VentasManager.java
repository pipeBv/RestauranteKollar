package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Plato;
import java.util.ArrayList;
import java.util.List;

public class VentasManager {

    public void registrarVenta(String nombrePlato, double precioVenta, int cantidad) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("platos_vendidos");
        Document doc = new Document("nombrePlato", nombrePlato)
                .append("precioVenta", precioVenta)
                .append("cantidad", cantidad)
                .append("total", precioVenta * cantidad);
        collection.insertOne(doc);
    }
    public List<Plato> cargarVentasReporte() {
        List<Plato> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("platos_vendidos");
        for (Document doc : collection.find()) {
            String nombre = doc.getString("nombrePlato");
            double precio = doc.get("precioVenta", Number.class).doubleValue();
            Plato p = new Plato(null, nombre, precio, "", null);
            lista.add(p);
        }
        return lista;
    }
}
