
package org.example.aplicacion;


import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
import org.example.entidades.Presupuesto;
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
    private Presupuesto presupuesto;

    public MenuEmpleado(InventarioManager inventarioManager, PlatoManager platoManager) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        this.presupuesto = new Presupuesto(10000000,0,0);
        initUI();
        cargarDatos();
    }

    private void initUI() {
        setTitle("Menú Empleado - Restaurante Kollar");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ver Platos", crearPanelCarta());
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
        String[] col = {"Nombre", "Precio","Descripción","Ingredientes"};
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
        String[] col = {"Nombre", "Stock", "Unidad", "Categoría"};
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
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Seleccionar Plato:"), gbc);
        JComboBox<String> comboPlatos = new JComboBox<>();
        List<Plato> platos = platoManager.cargarPlatos();
        for (Plato p : platos) {
            comboPlatos.addItem(p.getNombre());
        }
        gbc.gridx = 1;
        panel.add(comboPlatos, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Cantidad:"), gbc);
        JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1;
        panel.add(spinnerCantidad, gbc);
        JButton btnHacerPedido = new JButton("Confirmar Pedido");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnHacerPedido, gbc);
        JTextArea areaResultado = new JTextArea(5, 20);
        areaResultado.setEditable(false);
        gbc.gridy = 3;
        panel.add(new JScrollPane(areaResultado), gbc);
        btnHacerPedido.addActionListener(e -> {
            String nombrePlato = (String) comboPlatos.getSelectedItem();
            int cantidad = (int) spinnerCantidad.getValue();
            Plato platoSeleccionado = platos.stream()
                    .filter(p -> p.getNombre().equals(nombrePlato))
                    .findFirst()
                    .orElse(null);
            if (platoSeleccionado != null) {
                HacerPedidoPlato hacerPedido = new HacerPedidoPlato(inventarioManager, presupuesto);
                hacerPedido.realizarPedido(platoSeleccionado, cantidad);
                cargarDatos();
                areaResultado.setText("Último pedido: " + cantidad + "x " + nombrePlato + "\nPresupuesto actual: $" + presupuesto.getPresupuesto());
            }
        });

        return panel;
    }
    private void cargarDatos() {
        List<Plato> platos = platoManager.cargarPlatos();
        modeloTablaPlatos.setRowCount(0);
        for (Plato p : platos) {
            modeloTablaPlatos.addRow(new Object[]{p.getNombre(), p.getPrecio(), p.getDescripcion(), p.getListaIngredientes()});
        }
        List<Ingrediente> ingredientes = inventarioManager.cargarIngredientes();
        modeloTablaInventario.setRowCount(0);
        for (Ingrediente ing : ingredientes) {
            modeloTablaInventario.addRow(new Object[]{ing.getNombre(), ing.getStock(), ing.getUnidadMedida(),ing.getCategoria()});
        }
    }
}