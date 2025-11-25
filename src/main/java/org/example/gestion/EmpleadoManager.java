package org.example.gestion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.database.ConexionMongoDB;
import org.example.entidades.Empleado;
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
                System.out.println("Documento crudo: " + doc.toJson());
                Object idObj = doc.get("_id");
                int id = (idObj != null) ? idObj.hashCode() : 0;

                String nombre = doc.getString("nombre");
                String rolEmpleado = doc.getString("rolEmpleado");

                String contraseña = doc.getString("contraseña");
                if (contraseña == null) {
                    System.out.println("AVISO: El campo 'contraseña' es nulo para " + nombre);
                    contraseña = "";
                }

                double sueldo = 0.0;

                if (doc.containsKey("sueldo")) {
                    sueldo = doc.getDouble("sueldo");
                }

                Empleado emp = new Empleado(id, nombre, contraseña, rolEmpleado, sueldo);
                lista.add(emp);
                System.out.println("Empleado cargado: " + nombre); // Confirmación

            } catch (Exception e) {
                System.err.println("Error cargando un empleado específico: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Total empleados cargados: " + lista.size());
        return lista;
    }
}
