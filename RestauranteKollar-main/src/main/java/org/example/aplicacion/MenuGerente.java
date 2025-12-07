package org.example.aplicacion;

import org.example.entidades.Empleado;
import org.example.entidades.Ingrediente;
import org.example.entidades.Merma;
import org.example.entidades.Plato;
import org.example.entidades.Presupuesto;
import org.example.entidades.Proveedor;
import org.example.gestion.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MenuGerente extends JFrame {

    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private MermaManager mermaManager;
    private EmpleadoManager empleadoManager;
    private PresupuestoManager presupuestoManager;
    private ProveedorManager proveedorManager;

    private DefaultTableModel modeloTablaInventario;
    private DefaultTableModel modeloTablaPlatos;
    private DefaultTableModel modeloTablaPersonal;
    private DefaultTableModel modeloTablaMermas;

    private JLabel lblTotalMermas;
    private Presupuesto presupuesto;

    private DecimalFormat df = new DecimalFormat("$ #,##0");

    public MenuGerente(InventarioManager inventarioManager, PlatoManager platoManager, MermaManager mermaManager, EmpleadoManager empleadoManager) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        this.mermaManager = mermaManager;
        this.empleadoManager = empleadoManager;

        this.presupuestoManager = new PresupuestoManager();
        this.proveedorManager = new ProveedorManager();
        this.presupuesto = presupuestoManager.cargarPresupuesto();

        try {
            this.inventarioManager.auditarVencimientos();
        } catch (Exception e) {
            System.err.println("Error en auditoría automática: " + e.getMessage());
        }

        cargarInterfazPrincipal();
        cargarDatosTablas();
    }

    private void cargarInterfazPrincipal() {
        setTitle("Administración - Restaurante Kollar (Gerente)");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Administrar Inventario", crearPanelInventario());
        tabbedPane.addTab("Administrar Platos", crearPanelPlatos());
        tabbedPane.addTab("Administrar Empleados", crearPanelEmpleados());
        tabbedPane.addTab("Control de Mermas", crearPanelMermas());
        tabbedPane.addTab("Ver Reporte", crearPanelReporte());
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

    private JPanel crearPanelMermas() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Registrar Nueva Pérdida Manual"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Ingrediente:"), gbc);

        JComboBox<String> comboIngredientes = new JComboBox<>();
        List<Ingrediente> ingredientes = inventarioManager.cargarIngredientes();
        for (Ingrediente ing : ingredientes) {
            comboIngredientes.addItem(ing.getNombre());
        }
        gbc.gridx = 1;
        panelForm.add(comboIngredientes, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Cantidad perdida:"), gbc);
        JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 1000.0, 0.5));
        gbc.gridx = 1;
        panelForm.add(spinnerCantidad, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Motivo:"), gbc);
        JTextField txtMotivo = new JTextField(15);
        gbc.gridx = 1;
        panelForm.add(txtMotivo, gbc);

        JButton btnRegistrar = new JButton("Registrar Merma");
        btnRegistrar.setBackground(new Color(192, 57, 43));
        btnRegistrar.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(btnRegistrar, gbc);

        panelPrincipal.add(panelForm, BorderLayout.NORTH);

        String[] col = {"Ingrediente", "Cantidad", "Pérdida ($)", "Motivo", "Fecha"};
        modeloTablaMermas = new DefaultTableModel(col, 0);
        JTable tablaMermas = new JTable(modeloTablaMermas);

        tablaMermas.getColumnModel().getColumn(3).setPreferredWidth(200);

        panelPrincipal.add(new JScrollPane(tablaMermas), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalMermas = new JLabel("Total Dinero Perdido: $0");
        lblTotalMermas.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalMermas.setForeground(new Color(231, 76, 60));
        panelSur.add(lblTotalMermas, BorderLayout.WEST);

        JButton btnEliminar = new JButton("Eliminar Registro Seleccionado");
        btnEliminar.addActionListener(e -> {
            int row = tablaMermas.getSelectedRow();
            if(row != -1) {
                String motivo = (String) modeloTablaMermas.getValueAt(row, 3);
                mermaManager.eliminarMermaPorMotivo(motivo);
                cargarDatosTablas();
            }
        });
        panelSur.add(btnEliminar, BorderLayout.EAST);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> {
            String nombreIngrediente = (String) comboIngredientes.getSelectedItem();
            double cantidad = ((Number) spinnerCantidad.getValue()).doubleValue();
            String motivo = txtMotivo.getText();

            if (motivo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un motivo.");
                return;
            }

            Ingrediente ingredienteSeleccionado = ingredientes.stream()
                    .filter(i -> i.getNombre().equals(nombreIngrediente))
                    .findFirst()
                    .orElse(null);

            if (ingredienteSeleccionado != null) {
                double precioUnitario = obtenerPrecioCosto(nombreIngrediente);
                double totalPerdida = cantidad * precioUnitario;

                Merma nuevaMerma = new Merma(
                        ingredienteSeleccionado,
                        null,
                        "Merma " + ingredienteSeleccionado.getNombre(),
                        cantidad,
                        motivo,
                        LocalDate.now()
                );

                mermaManager.registrarMerma(nuevaMerma);
                inventarioManager.actualizarStock(nombreIngrediente, -cantidad);
                presupuestoManager.actualizarPresupuesto(0, 0, totalPerdida);

                JOptionPane.showMessageDialog(this, "Merma registrada. Pérdida calculada: " + df.format(totalPerdida));
                cargarDatosTablas();
                txtMotivo.setText("");
            }
        });

        return panelPrincipal;
    }

    private double obtenerPrecioCosto(String nombreIngrediente) {
        try {
            List<Proveedor> proveedores = proveedorManager.cargarProveedores();
            String nombreBuscado = nombreIngrediente.trim().toLowerCase();

            for (Proveedor p : proveedores) {
                String nombreProv = p.getNombreIngrediente().trim().toLowerCase();

                if (nombreProv.equals(nombreBuscado) ||
                        nombreProv.contains(nombreBuscado) ||
                        nombreBuscado.contains(nombreProv)) {

                    if (p.getPrecioPorUnidad() > 0) return p.getPrecioPorUnidad();
                    if (p.getPrecioPorKilo() > 0) return p.getPrecioPorKilo();
                    if (p.getPrecioPorMayor() > 0) return p.getPrecioPorMayor();
                }
            }
        } catch (Exception e) {
            System.err.println("Error buscando precio: " + e.getMessage());
        }
        return 0.0;
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
                    emp.getNombre(), emp.getRolEmpleado(), emp.getSueldo()
            });
        }

        List<Merma> mermasTodas = mermaManager.cargarMermas();
        double sumaTotalPerdidas = 0;

        if (modeloTablaMermas != null) {
            modeloTablaMermas.setRowCount(0); // Limpiar tabla

            List<Merma> mermasValidas = mermasTodas.stream()
                    .filter(m -> m.getIngrediente() != null && m.getIngrediente().getNombre() != null && !m.getIngrediente().getNombre().isEmpty())
                    .collect(Collectors.toList());

            mermasValidas.sort(Comparator.comparing(Merma::getFecha).reversed());

            for (Merma m : mermasValidas) {
                double precioUnitario = obtenerPrecioCosto(m.getIngrediente().getNombre());
                double costoTotalMerma = m.getCantidad() * precioUnitario;

                sumaTotalPerdidas += costoTotalMerma;

                modeloTablaMermas.addRow(new Object[]{
                        m.getIngrediente().getNombre(),
                        m.getCantidad(),
                        df.format(costoTotalMerma),
                        m.getMotivo(),
                        m.getFecha()
                });
            }
            // Actualizar el total
            lblTotalMermas.setText("Total Dinero Perdido: " + df.format(sumaTotalPerdidas));
        }
    }

    // --- RESTO DE PANELES ---

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Stock", "Unidad", "Categoría", "Caducidad"};
        modeloTablaInventario = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaInventario);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnOrdenar = new JButton("Ordenar Compra");
        btnOrdenar.addActionListener(e -> {
            this.presupuesto = presupuestoManager.cargarPresupuesto();
            OrdenarCompra ventanaCompra = new OrdenarCompra(inventarioManager, presupuesto);
            ventanaCompra.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    cargarDatosTablas();
                    presupuesto = presupuestoManager.cargarPresupuesto();
                }
            });
            ventanaCompra.setVisible(true);
        });
        panelBotones.add(btnOrdenar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPlatos() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Precio","Descripción", "Ingredientes"};
        modeloTablaPlatos = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPlatos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(e -> {
            JTextField nombreField = new JTextField();
            JTextField precioField = new JTextField();
            JTextField descField = new JTextField();
            Object[] message = {"Nombre:", nombreField, "Precio:", precioField, "Descripción:", descField};
            int option = JOptionPane.showConfirmDialog(this, message, "Agregar Plato", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    platoManager.agregarPlato(nombreField.getText(), Double.parseDouble(precioField.getText()), descField.getText());
                    cargarDatosTablas();
                } catch(NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Precio inválido"); }
            }
        });

        btnModificar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                String nombreActual = (String) modeloTablaPlatos.getValueAt(row, 0);
                double precioActual = (Double) modeloTablaPlatos.getValueAt(row, 1);
                String descActual = (String) modeloTablaPlatos.getValueAt(row, 2);
                JTextField precioField = new JTextField(String.valueOf(precioActual));
                JTextField descField = new JTextField(descActual);
                Object[] message = {"Nuevo Precio:", precioField, "Nueva Descripción:", descField};
                int option = JOptionPane.showConfirmDialog(this, message, "Modificar " + nombreActual, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        platoManager.modificarPlato(nombreActual, Double.parseDouble(precioField.getText()), descField.getText());
                        cargarDatosTablas();
                    } catch(NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Precio inválido"); }
                }
            } else { JOptionPane.showMessageDialog(this, "Seleccione un plato"); }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                String nombre = (String) modeloTablaPlatos.getValueAt(row, 0);
                platoManager.eliminarPlato(nombre);
                cargarDatosTablas();
            } else { JOptionPane.showMessageDialog(this, "Seleccione un plato"); }
        });

        panelBotones.add(btnAgregar); panelBotones.add(btnModificar); panelBotones.add(btnEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Rol Empleado", "Sueldo"};
        modeloTablaPersonal = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPersonal);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(e -> {
            JTextField nombreField = new JTextField();
            String[] roles = {"Cajero", "Mesero", "Cocinero", "Gerente"};
            JComboBox<String> rolBox = new JComboBox<>(roles);
            JTextField passField = new JTextField();
            JTextField sueldoField = new JTextField();
            Object[] message = {"Nombre:", nombreField, "Rol:", rolBox, "Contraseña:", passField, "Sueldo:", sueldoField};
            int option = JOptionPane.showConfirmDialog(this, message, "Agregar Empleado", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    empleadoManager.agregarEmpleado(nombreField.getText(), (String)rolBox.getSelectedItem(), passField.getText(), Double.parseDouble(sueldoField.getText()));
                    cargarDatosTablas();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Datos inválidos"); }
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                String nombre = (String) modeloTablaPersonal.getValueAt(row, 0);
                empleadoManager.eliminarEmpleado(nombre);
                cargarDatosTablas();
            }
        });

        btnModificar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                String nombre = (String) modeloTablaPersonal.getValueAt(row, 0);
                String[] roles = {"Cajero", "Mesero", "Cocinero", "Gerente"};
                JComboBox<String> rolBox = new JComboBox<>(roles);
                JTextField sueldoField = new JTextField();
                Object[] message = { "Nuevo Rol:", rolBox, "Nuevo Sueldo:", sueldoField };
                int option = JOptionPane.showConfirmDialog(this, message, "Modificar " + nombre, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        empleadoManager.modificarEmpleado(nombre, (String)rolBox.getSelectedItem(), Double.parseDouble(sueldoField.getText()));
                        cargarDatosTablas();
                    } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error en datos"); }
                }
            }
        });

        panelBotones.add(btnAgregar); panelBotones.add(btnModificar); panelBotones.add(btnEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);
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
        JButton btnConfirmar = new JButton("Realizar Pedido");
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
                HacerPedidoPlato gestorPedidos = new HacerPedidoPlato(inventarioManager, presupuestoManager);
                gestorPedidos.realizarPedido(platoSeleccionado, cantidad);
                cargarDatosTablas();
            }
        });
        return panel;
    }

    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new GridBagLayout());
        JButton btnGenerarReporte = new JButton("Abrir Reporte General");
        btnGenerarReporte.setFont(new Font("Arial", Font.BOLD, 16));
        btnGenerarReporte.setPreferredSize(new Dimension(250, 50));

        btnGenerarReporte.addActionListener(e -> {
            VentasManager ventasManager = new VentasManager();
            List<Plato> platosVendidos = ventasManager.cargarVentasReporte();
            Reporte reporteWindow = new Reporte(inventarioManager, platoManager, mermaManager, platosVendidos);
            reporteWindow.setVisible(true);
        });
        panel.add(btnGenerarReporte);
        return panel;
    }
}