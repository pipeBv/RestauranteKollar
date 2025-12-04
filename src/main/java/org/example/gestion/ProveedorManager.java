package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Proveedor;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ProveedorManager {

    public List<Proveedor> cargarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("precio_proveedores");

        for (Document doc : collection.find()) {
            try {
                String nombreIngrediente = doc.getString("nombreIngrediente");
                String categoria = doc.getString("categoria");
                
                double pUnidad = doc.get("precioUnidad", Number.class).doubleValue();
                double pKilo = doc.get("precioKilo", Number.class).doubleValue();
                double pMayor = doc.get("precioPorMayor", Number.class).doubleValue();

                java.util.Date fechaDate = doc.getDate("fechaCaducidad");
                java.time.LocalDate fecha = (fechaDate != null) ?
                        fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                Proveedor p = new Proveedor(pUnidad, pKilo, pMayor, nombreIngrediente, categoria, fecha);
                lista.add(p);
            } catch (Exception e) {
                System.err.println("Error al cargar proveedor: " + e.getMessage());
            }
        }
        return lista;
    }
}
