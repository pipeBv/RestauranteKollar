package org.example.aplicacion;

import org.example.entidades.Empleado;
import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
import org.example.entidades.Presupuesto;
import org.example.gestion.EmpleadoManager;
import org.example.gestion.InventarioManager;
import org.example.gestion.MermaManager;
import org.example.gestion.PlatoManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuGerente extends JFrame {

    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private MermaManager mermaManager;
    private EmpleadoManager empleadoManager;
    private DefaultTableModel modeloTablaInventario;
    private DefaultTableModel modeloTablaPlatos;
    private DefaultTableModel modeloTablaPersonal;
    private Presupuesto presupuesto;

    public MenuGerente(InventarioManager inventarioManager, PlatoManager platoManager, MermaManager mermaManager, EmpleadoManager empleadoManager) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        this.mermaManager = mermaManager;
        this.empleadoManager = empleadoManager;
        this.presupuesto = new Presupuesto(10000000,0,0);

        cargarInterfazPrincipal();
        cargarDatosTablas();
    }

    private void cargarInterfazPrincipal() {
        setTitle("Administración - Restaurante Kollar (Gerente)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Administrar Inventario", crearPanelInventario());
        tabbedPane.addTab("Administrar Platos", crearPanelPlatos());
        tabbedPane.addTab("Administrar Empleados", crearPanelEmpleados());
        tabbedPane.addTab("Ver reporte", crearPanelReporte());
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

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Stock", "Unidad", "Categoría", "Caducidad"};
        modeloTablaInventario = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaInventario);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        panelBotones.add(new JButton("Ordenar Compra"));
        OrdenarCompra ordenarCompra = new OrdenarCompra();

        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPlatos() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Precio","Descripción", "Ingredientes"};
        modeloTablaPlatos = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPlatos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Rol Empleado"};
        modeloTablaPersonal = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPersonal);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Plato:"), gbc);
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
        JButton btnConfirmar = new JButton("Realizar " +
                "Pedido");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(btnConfirmar, gbc);
        btnConfirmar.addActionListener(e -> {
            String nombrePlato = (String) comboPlatos.getSelectedItem();
            int cantidad = (int) spinnerCantidad.getValue();
            Plato platoSeleccionado = platos.stream()
                    .filter(p -> p.getNombre().equals(nombrePlato))
                    .findFirst()
                    .orElse(null);
            if (platoSeleccionado != null) {
                HacerPedidoPlato gestorPedidos = new HacerPedidoPlato(inventarioManager, presupuesto);
                gestorPedidos.realizarPedido(platoSeleccionado, cantidad);
                cargarDatosTablas();
            }
        });

        return panel;
    }

    private JPanel crearPanelReporte() {
        return new JPanel();
    }

    private void cargarDatosTablas() {
        List<Ingrediente> ingredientes = inventarioManager.cargarIngredientes();
        modeloTablaInventario.setRowCount(0);
        for (Ingrediente ing : ingredientes) {
            modeloTablaInventario.addRow(new Object[]{
                    ing.getNombre(), ing.getStock(), ing.getUnidadMedida(), ing.getCategoria(), ing.getFechaCaducidad()});
        }

        List<Plato> platos = platoManager.cargarPlatos();
        modeloTablaPlatos.setRowCount(0);
        for (Plato p : platos) {
            modeloTablaPlatos.addRow(new Object[]{
                    p.getNombre(), p.getPrecio(), p.getDescripcion(), p.getListaIngredientes()});
        }


        List<Empleado> empleados = empleadoManager.cargarEmpleados();
        modeloTablaPersonal.setRowCount(0);
        for (Empleado emp : empleados) {
            modeloTablaPersonal.addRow(new Object[]{
                    emp.getNombre(), emp.getRolEmpleado()
            });
        }
    }
}