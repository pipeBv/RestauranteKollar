package org.example.aplicacion;

import org.example.entidades.Plato;
import org.example.gestion.InventarioManager;
import org.example.gestion.PresupuestoManager;
import org.example.gestion.VentasManager;

import javax.swing.*;

public class HacerPedidoPlato {
    private InventarioManager inventarioManager;
    private PresupuestoManager presupuestoManager;
    private VentasManager ventasManager;

    public HacerPedidoPlato(InventarioManager inventarioManager, PresupuestoManager presupuestoManager) {
        this.inventarioManager = inventarioManager;
        this.presupuestoManager = presupuestoManager;
        this.ventasManager = new VentasManager();
    }

    public boolean realizarPedido(Plato plato, int cantidad) {
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.");
            return false;
        }
        if (!inventarioManager.verificarStock(plato.getListaIngredientes(), cantidad)) {
            JOptionPane.showMessageDialog(null, "No hay suficientes ingredientes para preparar " + cantidad + " orden(es) de " + plato.getNombre());
            return false;
        }

        inventarioManager.descontarStock(plato.getListaIngredientes(), cantidad);

        double totalVenta = plato.getPrecio() * cantidad;

        presupuestoManager.actualizarPresupuesto(totalVenta, totalVenta, 0);

        // 3. Registrar Venta en BD (platos_vendidos)
        ventasManager.registrarVenta(plato.getNombre(), plato.getPrecio(), cantidad);

        JOptionPane.showMessageDialog(null, "¡Venta exitosa!, Total vendido: $" + totalVenta);

        return true;

    }
}