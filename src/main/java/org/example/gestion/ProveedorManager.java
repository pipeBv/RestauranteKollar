package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Proveedor;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProveedorManager {

    public List<Proveedor> cargarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("precios_proveedores");
        for (Document doc : collection.find()) {
            try {
                String nombreIngrediente = doc.getString("nombreIngrediente");
                String categoria = doc.getString("categoria");
                Double pUnidad = doc.get("precioUnidad", Number.class) != null ?
                        doc.get("precioUnidad", Number.class).doubleValue() : 0.0;
                Double pKilo = doc.get("precioKilo", Number.class) != null ?
                        doc.get("precioKilo", Number.class).doubleValue() : 0.0;
                Double pMayor = doc.get("precioMayor", Number.class) != null ?
                        doc.get("precioMayor", Number.class).doubleValue() : 0.0;
                java.util.Date fechaDate = doc.getDate("fechaCaducidad");
                java.time.LocalDate fecha = (fechaDate != null) ?
                        fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                Proveedor p = new Proveedor(pUnidad, pKilo, pMayor, nombreIngrediente, categoria, fecha);
                lista.add(p);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al cargar proveedor: " + e.getMessage());
            }
        }
        return lista;
    }
}
