package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Merma;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
public class MermaManager {
    public List<Merma> cargarMerma() {
        List<Merma> listaMermas = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("mermas");

        for (Document doc : collection.find()) {
            try {
                int id = doc.getObjectId("_id").hashCode();
                String nombre = doc.getString("nombre");
                int cantidad = doc.get("cantidad", Number.class).intValue();
                String motivo = doc.getString("causa");
                java.util.Date fechaDate = doc.getDate("fecha");
                java.time.LocalDate fecha = (fechaDate != null) ?
                        fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

                Merma merma = new Merma(id, nombre, cantidad, motivo, fecha);
                listaMermas.add(merma);
            } catch (Exception e) {
                System.err.println("Error cargando merma: " + e.getMessage());
            }
        }
        return listaMermas;
    }
}
