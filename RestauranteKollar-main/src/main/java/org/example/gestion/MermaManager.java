package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Ingrediente;
import org.example.entidades.Merma;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MermaManager {

    public List<Merma> cargarMermas() {
        List<Merma> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("mermas");

        InventarioManager inventarioManager = new InventarioManager();
        List<Ingrediente> inventario = inventarioManager.cargarIngredientes();

        for (Document doc : collection.find()) {
            try {
                Object idObj = doc.get("_id");
                int id = (idObj != null) ? idObj.hashCode() : 0;

                String nombreIngrediente = doc.getString("nombreIngrediente");

                Number cantNum = doc.get("cantidad", Number.class);
                double cantidad = (cantNum != null) ? cantNum.doubleValue() : 0.0;

                String motivo = doc.getString("motivo");
                java.util.Date fechaDate = doc.getDate("fecha");
                LocalDate fecha = (fechaDate != null) ?
                        fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.now();

                Ingrediente ingredienteEncontrado = inventario.stream()
                        .filter(ing -> ing.getNombre().equalsIgnoreCase(nombreIngrediente))
                        .findFirst()
                        .orElse(new Ingrediente(0, nombreIngrediente, 0, "N/A", "Desconocido", null));

                Merma merma = new Merma(ingredienteEncontrado, id, "Merma " + nombreIngrediente, cantidad, motivo, fecha);
                lista.add(merma);
            } catch (Exception e) {
                System.err.println("Error cargando merma: " + doc.toJson() + " -> " + e.getMessage());
            }
        }
        return lista;
    }

    public void registrarMerma(Merma merma) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("mermas");

        Document doc = new Document("nombreIngrediente", merma.getIngrediente().getNombre())
                .append("cantidad", merma.getCantidad())
                .append("motivo", merma.getMotivo())
                .append("fecha", java.sql.Date.valueOf(merma.getFecha()));

        collection.insertOne(doc);
    }

        public void eliminarMerma(Object id) {

        }

        public void eliminarMermaPorMotivo(String motivo) {
            MongoDatabase db = ConexionMongoDB.getDatabase();
            MongoCollection<Document> collection = db.getCollection("mermas");
            collection.deleteOne(new Document("motivo", motivo));
        }
    }