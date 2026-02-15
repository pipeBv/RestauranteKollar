package org.example.aplicacion;

import org.example.entidades.Empleado;
import org.example.entidades.Ingrediente;
import org.example.entidades.Merma;
import org.example.entidades.Plato;
import org.example.entidades.Presupuesto;
import org.example.gestion.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MenuGerente extends JFrame {

    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private MermaManager mermaManager;
    private EmpleadoManager empleadoManager;
    private PresupuestoManager presupuestoManager;
    private DefaultTableModel modeloTablaInventario;
    private DefaultTableModel modeloTablaPlatos;
    private DefaultTableModel modeloTablaPersonal;
    private DefaultTableModel modeloTablaMermas; // Nuevo modelo para mermas
    private Presupuesto presupuesto;

    public MenuGerente(InventarioManager inventarioManager, PlatoManager platoManager, MermaManager mermaManager, EmpleadoManager empleadoManager) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        this.mermaManager = mermaManager;
        this.empleadoManager = empleadoManager;

        this.presupuestoManager = new PresupuestoManager();
        this.presupuesto = presupuestoManager.cargarPresupuesto();

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
        tabbedPane.addTab("Registrar Mermas", crearPanelMermas()); // Nueva pestaña
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

    private JPanel crearPanelMermas() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // --- Parte Superior: Formulario ---
        JPanel panelForm = new JPanel(new GridBagLayout());
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
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(btnRegistrar, gbc);

        panelPrincipal.add(panelForm, BorderLayout.NORTH);

        // --- Parte Central: Tabla de Mermas ---
        String[] col = {"Ingrediente", "Cantidad", "Motivo", "Fecha"};
        modeloTablaMermas = new DefaultTableModel(col, 0);
        JTable tablaMermas = new JTable(modeloTablaMermas);
        panelPrincipal.add(new JScrollPane(tablaMermas), BorderLayout.CENTER);

        // --- Parte Inferior: Botón Eliminar ---
        JPanel panelSur = new JPanel();
        JButton btnEliminar = new JButton("Eliminar Seleccionada");
        btnEliminar.addActionListener(e -> {
            int row = tablaMermas.getSelectedRow();
            if(row != -1) {
                String motivo = (String) modeloTablaMermas.getValueAt(row, 2);
                mermaManager.eliminarMermaPorMotivo(motivo);
                cargarDatosTablas();
            }
        });
        panelSur.add(btnEliminar);
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
                // Actualizar pérdidas en presupuesto (ejemplo: costo estimado arbitrario o 0 si no tenemos precios)
                presupuestoManager.actualizarPresupuesto(0, 0, cantidad * 5.0); // Asumiendo costo prom 5.0

                JOptionPane.showMessageDialog(this, "Merma registrada correctamente.");
                cargarDatosTablas();
                txtMotivo.setText("");
            }
        });

        return panelPrincipal;
    }

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
            Object[] message = {
                    "Nombre:", nombreField,
                    "Precio:", precioField,
                    "Descripción:", descField
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Agregar Plato", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    platoManager.agregarPlato(nombreField.getText(), Double.parseDouble(precioField.getText()), descField.getText());
                    cargarDatosTablas();
                } catch(NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Precio inválido");
                }
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
                Object[] message = {
                        "Nuevo Precio:", precioField,
                        "Nueva Descripción:", descField
                };
                int option = JOptionPane.showConfirmDialog(this, message, "Modificar Plato: " + nombreActual, JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        platoManager.modificarPlato(nombreActual, Double.parseDouble(precioField.getText()), descField.getText());
                        cargarDatosTablas();
                    } catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Precio inválido");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un plato");
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                String nombre = (String) modeloTablaPlatos.getValueAt(row, 0);
                platoManager.eliminarPlato(nombre);
                cargarDatosTablas();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un plato");
            }
        });

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelEmpleados() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"Nombre", "Rol Empleado", "Sueldo"}; // Agregado Sueldo
        modeloTablaPersonal = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPersonal);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(e -> {
            JTextField nombreField = new JTextField();
            String[] roles = {"Cajero", "Mesero", "Cocinero"};
            JComboBox<String> rolBox = new JComboBox<>(roles);
            JTextField passField = new JTextField();
            JTextField sueldoField = new JTextField();
            Object[] message = {
                    "Nombre:", nombreField,
                    "Rol:", rolBox,
                    "Contraseña:", passField,
                    "Sueldo:", sueldoField
            };
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
                // Simplificado: Solo modificar Rol y Sueldo por nombre
                String[] roles = {"Cajero", "Mesero", "Cocinero"};
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

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
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
                // Usar HacerPedidoPlato
                HacerPedidoPlato gestorPedidos = new HacerPedidoPlato(inventarioManager, presupuestoManager);
                gestorPedidos.realizarPedido(platoSeleccionado, cantidad);
                cargarDatosTablas();
            }
        });

        return panel;
    }

    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnGenerarReporte = new JButton("Generar Reporte General");
        btnGenerarReporte.setFont(new Font("Arial", Font.BOLD, 16));
        btnGenerarReporte.setPreferredSize(new Dimension(250, 50));

        btnGenerarReporte.addActionListener(e -> {
            VentasManager ventasManager = new VentasManager();
            List<Plato> platosVendidos = ventasManager.cargarVentasReporte();

            Reporte reporteWindow = new Reporte(inventarioManager, platoManager, mermaManager, platosVendidos);
            reporteWindow.setVisible(true);
        });

        panel.add(btnGenerarReporte, gbc);
        return panel;
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
            // Asumiendo que no tienes un getter sueldo publico visible en el contexto pero lo agregamos en la tabla
            modeloTablaPersonal.addRow(new Object[]{
                    emp.getNombre(), emp.getRolEmpleado(), 0.0 // Ajustar si tienes getter de sueldo
            });
        }

        List<Merma> mermas = mermaManager.cargarMermas();
        if (modeloTablaMermas != null) {
            modeloTablaMermas.setRowCount(0);
            for (Merma m : mermas) {
                modeloTablaMermas.addRow(new Object[]{
                        m.getIngrediente().getNombre(), m.getCantidad(), m.getMotivo(), m.getFecha()
                });
            }
        }
    }
}
