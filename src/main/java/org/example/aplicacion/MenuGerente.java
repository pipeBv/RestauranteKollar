package org.example.aplicacion;

import org.example.entidades.Empleado;
import org.example.entidades.Ingrediente;
import org.example.entidades.Plato;
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

    public MenuGerente(InventarioManager im, PlatoManager pm, MermaManager mm, EmpleadoManager em) {
        this.inventarioManager = im;
        this.platoManager = pm;
        this.mermaManager = mm;
        this.empleadoManager = em;

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
        String[] col = {"Nombre", "Stock", "Unidad", "Categoría"};
        modeloTablaInventario = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaInventario);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        panelBotones.add(new JButton("Agregar Item"));
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelPlatos() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Precio"};
        modeloTablaPlatos = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPlatos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"ID", "Nombre", "Puesto"};
        modeloTablaPersonal = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPersonal);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPedidos() {
        return new JPanel();
    }

    private JPanel crearPanelReporte() {
        return new JPanel();
    }

    private void cargarDatosTablas() {
        List<Ingrediente> ingredientes = inventarioManager.cargarIngredientes();
        modeloTablaInventario.setRowCount(0);
        for (Ingrediente ing : ingredientes) {
            modeloTablaInventario.addRow(new Object[]{
                    ing.getNombre(), ing.getStock(), ing.getUnidadMedida(), ing.getCategoria()
            });
        }

        List<Plato> platos = platoManager.cargarPlatos();
        modeloTablaPlatos.setRowCount(0);
        for (Plato p : platos) {
            modeloTablaPlatos.addRow(new Object[]{ p.getNombre(), p.getPrecio() });
        }


        List<Empleado> empleados = empleadoManager.cargarEmpleados();
        modeloTablaPersonal.setRowCount(0);
        for (Empleado emp : empleados) {
            modeloTablaPersonal.addRow(new Object[]{
                    emp.getId(), emp.getNombre(), emp.getRolEmpleado()
            });
        }
    }
}