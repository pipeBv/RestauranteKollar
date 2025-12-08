package org.example.gestion;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Compra;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ComprasManager {

    public void registrarCompra(Compra compra) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("historial_compras");
        Document doc = new Document("ingrediente", compra.getIngrediente())
                .append("cantidad", compra.getCantidad())
                .append("tipoCompra", compra.getTipoCompra())
                .append("totalGastado", compra.getTotalGastado())
                .append("fecha", java.sql.Date.valueOf(compra.getFecha()));
        collection.insertOne(doc);
    }

    public List<Compra> cargarCompras() {
        List<Compra> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("historial_compras");
        for (Document doc : collection.find()) {
            try {
                String ing = doc.getString("ingrediente");
                double cant = doc.getDouble("cantidad");
                String tipo = doc.getString("tipoCompra");
                double total = doc.getDouble("totalGastado");
                java.util.Date fechaDate = doc.getDate("fecha");
                LocalDate fecha = (fechaDate != null) ?
                        fechaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : LocalDate.now();
                lista.add(new Compra(ing, cant, tipo, total, fecha));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error leyendo compra: " + e.getMessage());
            }
        }
        return lista;
    }
}