
package org.example.aplicacion;

import org.example.entidades.Empleado;
import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
import org.example.gestion.InventarioManager;
import org.example.gestion.PlatoManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuEmpleado extends JFrame {
    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private DefaultTableModel modeloTablaPlatos;
    private DefaultTableModel modeloTablaInventario;

    public MenuEmpleado(InventarioManager inventarioManager, PlatoManager platoManager) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setTitle("Menú Empleado - Restaurante Kollar");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ver Carta", crearPanelCarta());
        tabbedPane.addTab("Ver Inventario", crearPanelInventario());
        tabbedPane.addTab("Hacer Pedido", crearPanelPedidos());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> {
            new RestauranteGUI().setVisible(true);
            dispose();
        });
        mainPanel.add(btnLogout, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel crearPanelCarta() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Precio"};
        modeloTablaPlatos = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTablaPlatos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Stock", "Unidad"};
        modeloTablaInventario = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTablaInventario);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea areaPedido = new JTextArea("Área para registrar pedido...");
        areaPedido.setEditable(false);
        panel.add(new JScrollPane(areaPedido), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        JButton btnNuevoPedido = new JButton("Realizar Nuevo Pedido");
        btnNuevoPedido.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Iniciando proceso de pedido...");
        });
        panelBotones.add(btnNuevoPedido);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarDatos() {
        List<Plato> platos = platoManager.cargarPlatos();
        modeloTablaPlatos.setRowCount(0);
        for (Plato p : platos) {
            modeloTablaPlatos.addRow(new Object[]{p.getNombre(), p.getPrecio()});
        }

        List<Ingrediente> ingredientes = inventarioManager.cargarIngredientes();
        modeloTablaInventario.setRowCount(0);
        for (Ingrediente ing : ingredientes) {
            modeloTablaInventario.addRow(new Object[]{ing.getNombre(), ing.getStock(), ing.getUnidadMedida()});
        }
    }
}