package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Presupuesto;
public class PresupuestoManager {

    public Presupuesto cargarPresupuesto() {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("presupuesto");
        Document doc = collection.find().first();

        if (doc != null) {
            double presupuesto = doc.get("presupuesto", Number.class).doubleValue();
            double perdidas = doc.get("perdidas", Number.class).doubleValue();
            double ganancias = doc.get("ganancias", Number.class).doubleValue();
            return new Presupuesto(presupuesto, perdidas, ganancias);
        } else {
            Presupuesto nuevo = new Presupuesto(10000000, 0, 0);
            guardarPresupuesto(nuevo);
            return nuevo;
        }
    }

    public void guardarPresupuesto(Presupuesto presupuesto) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("presupuesto");

        collection.deleteMany(new Document());

        Document doc = new Document("presupuesto", presupuesto.getPresupuesto())
                .append("perdidas", presupuesto.getPerdidas())
                .append("ganancias", presupuesto.getGanancias());
        collection.insertOne(doc);
    }

    public void actualizarPresupuesto(double cambioPresupuesto, double cambioGanancias, double cambioPerdidas) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("presupuesto");

        Document update = new Document("$inc", new Document("presupuesto", cambioPresupuesto)
                .append("ganancias", cambioGanancias)
                .append("perdidas", cambioPerdidas));
        collection.updateOne(new Document(), update);
    }
}
