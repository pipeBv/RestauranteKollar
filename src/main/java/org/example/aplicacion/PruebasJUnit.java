package org.example.aplicacion;

import org.example.entidades.*;
import org.junit.jupiter.api.Test;

import static com.mongodb.assertions.Assertions.assertNull;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PruebasJUnit {

    @Test
    public void testCreacionEmpleado() {
        Empleado empleado = new Empleado(1, "Juan Perez", "secret123", "Cajero", 500000.0);
        assertEquals("Juan Perez", empleado.getNombre());
        assertEquals("Cajero", empleado.getRolEmpleado());
        assertEquals("secret123", empleado.getContrasenia());
        assertNotNull(empleado.getId());
    }

    @Test
    public void testCreacionPlato() {
        List<Ingrediente> ingredientes = new ArrayList<>();
        Plato plato = new Plato(10, "Hamburguesa", 15000.0, "Con queso", ingredientes);
        assertEquals("Hamburguesa", plato.getNombre());
        assertEquals(15000.0, plato.getPrecio());
        assertTrue(plato.getListaIngredientes().isEmpty());
    }

    @Test
    public void testModificarPlato() {
        Plato plato = new Plato(10, "Pizza", 20000.0, "Napolitana", new ArrayList<>());
        plato.setPrecio(22000.0);
        plato.setDescripcion("Napolitana con extra queso");
        assertEquals(22000.0, plato.getPrecio());
        assertEquals("Napolitana con extra queso", plato.getDescripcion());
    }

    @Test
    public void testStockIngrediente() {
        Ingrediente tomate = new Ingrediente(5, "Tomate", 10.0, "Kg", "Verdura", LocalDate.now());
        tomate.setStock(tomate.getStock() - 2.5);
        assertEquals(7.5, tomate.getStock(), 0.01);
    }
    @Test
    public void testDatosCompra() {
        LocalDate fecha = LocalDate.of(2023, 10, 1);
        Compra compra = new Compra("Harina", 50.0, "Mayor", 100000.0, fecha);
        assertEquals("Harina", compra.getIngrediente());
        assertEquals(100000.0, compra.getTotalGastado());
        assertEquals(50.0, compra.getCantidad());
        assertEquals(fecha, compra.getFecha());
    }
    @Test
    public void testEstadoPresupuesto() {
        Presupuesto presupuesto = new Presupuesto(1000000.0, 50000.0, 200000.0);
        assertEquals(1000000.0, presupuesto.getPresupuesto());
        assertEquals(50000.0, presupuesto.getPerdidas());
        assertEquals(200000.0, presupuesto.getGanancias());
    }

    @Test
    public void testRegistroMerma() {
        Ingrediente lechuga = new Ingrediente(2, "Lechuga", 5.0, "Unidad", "Verdura", LocalDate.now());
        Merma merma = new Merma(lechuga, 99, "Merma Lechuga", 2.0, "Caducado", LocalDate.now());
        assertEquals("Caducado", merma.getMotivo());
        assertEquals(2.0, merma.getCantidad());
        assertEquals("Lechuga", merma.getIngrediente().getNombre());
    }

    @Test
    public void testProveedorPrecios() {
        Proveedor proveedor = new Proveedor(500.0, 4500.0, 40000.0, "Carne", "Carniceria", LocalDate.now().plusDays(30));
        assertEquals(4500.0, proveedor.getPrecioPorKilo());
        assertEquals("Carne", proveedor.getNombreIngrediente());
    }

    @Test
    public void testGerenteEsUsuario() {
        Gerente gerente = new Gerente(1, "Admin", "admin123");
        assertTrue(gerente instanceof Usuario);
        assertEquals("Admin", gerente.getNombre());
    }
    @Test
    public void testCambioRolEmpleado() {
        Empleado empleado = new Empleado(2, "Ana", "claveSegura", "Ayudante", 420000.0);
        empleado.setRolEmpleado("Jefe de Cocina");
        assertEquals("Jefe de Cocina", empleado.getRolEmpleado());
    }
}
