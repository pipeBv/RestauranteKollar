package org.example.aplicacion;

import org.example.entidades.Plato;
import org.example.entidades.Presupuesto;
import org.example.gestion.InventarioManager;

import javax.swing.*;

public class HacerPedidoPlato {
    private InventarioManager inventarioManager;
    private Presupuesto presupuesto;

    public HacerPedidoPlato(InventarioManager inventarioManager, Presupuesto presupuesto) {
        this.inventarioManager = inventarioManager;
        this.presupuesto = presupuesto;
    }

    public void realizarPedido(Plato plato, int cantidad) {
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.");
            return;
        }
        if (!inventarioManager.verificarStock(plato.getListaIngredientes(), cantidad)) {
            JOptionPane.showMessageDialog(null, "No hay suficientes ingredientes en el inventario para preparar " + cantidad + " orden(es) de " + plato.getNombre());
            return;
        }
        inventarioManager.descontarStock(plato.getListaIngredientes(), cantidad);
        double totalVenta = plato.getPrecio() * cantidad;
        presupuesto.setPresupuesto(presupuesto.getPresupuesto() + totalVenta);
        presupuesto.setGanancias(presupuesto.getGanancias() + totalVenta);
        JOptionPane.showMessageDialog(null, "Pedido realizado, total sumado al presupuesto: $" + totalVenta);
    }
}