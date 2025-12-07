package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Empleado;
import org.example.entidades.Gerente; // Importar Gerente

import java.util.ArrayList;
import java.util.List;

public class EmpleadoManager {
    public List<Empleado> cargarEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("empleados");
        System.out.println("Cargando empleados...");
        for (Document doc : collection.find()) {
            try {
                Object idObj = doc.get("_id");
                int id = (idObj != null) ? idObj.hashCode() : 0;
                String nombre = doc.getString("nombre");
                String rol = doc.getString("rolEmpleado");
                String pass = doc.getString("contraseña");
                // Manejo de nulos
                if (pass == null) pass = "";

                Double sueldo = doc.getDouble("sueldo");
                double sueldoVal = (sueldo != null) ? sueldo : 0.0;

                lista.add(new Empleado(id, nombre, pass, rol, sueldoVal));
            } catch (Exception e) {
                System.err.println("Error cargando un empleado específico: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Total empleados cargados: " + lista.size());
        return lista;
    }

    // Nuevo método para cargar gerentes
    public List<Gerente> cargarGerentes() {
        List<Gerente> lista = new ArrayList<>();
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("gerentes");

        for (Document doc : collection.find()) {
            try {
                Object idObj = doc.get("_id");
                int id = (idObj != null) ? idObj.hashCode() : 0;
                String nombre = doc.getString("nombre");
                String pass = doc.getString("contraseña");
                // Asegúrate que en BD el campo sea "contraseña" o "password"

                lista.add(new Gerente(id, nombre, pass));
            } catch (Exception e) {
                System.err.println("Error cargando gerente: " + e.getMessage());
            }
        }
        return lista;
    }

    public void agregarEmpleado(String nombre, String rol, String password, double sueldo) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("empleados");
        Document doc = new Document("nombre", nombre)
                .append("rolEmpleado", rol)
                .append("contraseña", password)
                .append("sueldo", sueldo);
        collection.insertOne(doc);
    }

    public void eliminarEmpleado(String nombre) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("empleados");
        collection.deleteOne(new Document("nombre", nombre));
    }

    public void modificarEmpleado(String nombreOriginal, String nuevoRol, double nuevoSueldo) {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        MongoCollection<Document> collection = db.getCollection("empleados");
        collection.updateOne(new Document("nombre", nombreOriginal),
                new Document("$set", new Document("rolEmpleado", nuevoRol).append("sueldo", nuevoSueldo)));
    }
}