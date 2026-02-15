package org.example.aplicacion;

import org.example.entidades.Ingrediente;
import org.example.entidades.Presupuesto;
import org.example.entidades.Proveedor;
import org.example.gestion.InventarioManager;
import org.example.gestion.PresupuestoManager; // Nuevo import
import org.example.gestion.ProveedorManager;
import org.example.entidades.Compra;
import org.example.gestion.ComprasManager;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrdenarCompra extends JFrame {
    private JPanel panelPrincipal;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private JSpinner spinnerCantidad;
    private JComboBox<String> comboTipoCompra;
    private JLabel lblPresupuestoActual;
    private InventarioManager inventarioManager;
    private Presupuesto presupuesto;
    private PresupuestoManager presupuestoManager;
    private List<Proveedor> listaProveedores;

    public OrdenarCompra(InventarioManager inventarioManager, Presupuesto presupuestoActual) {
        this.inventarioManager = inventarioManager;
        this.presupuesto = presupuestoActual;
        this.presupuestoManager = new PresupuestoManager();
        ProveedorManager provManager = new ProveedorManager();
        this.listaProveedores = provManager.cargarProveedores();

        if (this.listaProveedores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron proveedores en la base de datos.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        inicializarUI();
        actualizarTabla();
        actualizarLabelPresupuesto();
    }

    private void inicializarUI() {
        setTitle("Ordenar Compra a Proveedores");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panelPrincipal = new JPanel(new BorderLayout());


        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblTitulo = new JLabel("Ofertas de Proveedores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));

        lblPresupuestoActual = new JLabel("Presupuesto: $0.0");
        lblPresupuestoActual.setFont(new Font("Arial", Font.BOLD, 14));
        lblPresupuestoActual.setForeground(new Color(0, 100, 0));

        panelSuperior.add(lblTitulo, BorderLayout.CENTER);
        panelSuperior.add(lblPresupuestoActual, BorderLayout.EAST);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);


        String[] columnas = {"Ingrediente", "Categoría", "Precio Unidad", "Precio Kilo", "Precio Mayor", "Caducidad"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelPrincipal.add(new JScrollPane(tablaProveedores), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Realizar Pedido"));

        panelInferior.add(new JLabel("Cantidad:"));
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        panelInferior.add(spinnerCantidad);

        panelInferior.add(new JLabel("Tipo Compra:"));
        comboTipoCompra = new JComboBox<>(new String[]{"Unidad", "Kilo", "Mayor"});
        panelInferior.add(comboTipoCompra);

        JButton btnComprar = new JButton("Confirmar Compra");
        btnComprar.setBackground(new Color(70, 130, 180));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.addActionListener(e -> realizarCompra());
        panelInferior.add(btnComprar);

        JButton btnCerrar = new JButton("Cancelar");
        btnCerrar.addActionListener(e -> dispose());
        panelInferior.add(btnCerrar);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void realizarCompra() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un proveedor de la tabla.");
            return;
        }

        Proveedor proveedor = listaProveedores.get(filaSeleccionada);
        int cantidad = (int) spinnerCantidad.getValue();
        String tipoCompra = (String) comboTipoCompra.getSelectedItem();

        double costoTotal = 0;
        switch (tipoCompra) {
            case "Unidad": costoTotal = proveedor.getPrecioPorUnidad() * cantidad; break;
            case "Kilo":   costoTotal = proveedor.getPrecioPorKilo() * cantidad; break;
            case "Mayor":  costoTotal = proveedor.getPrecioPorMayor() * cantidad; break;
        }

        if (presupuesto.getPresupuesto() < costoTotal) {
             JOptionPane.showMessageDialog(this,
                    "Fondos insuficientes.\nCosto: $" + costoTotal + "\nPresupuesto: $" + presupuesto.getPresupuesto(),
                    "Error de Compra", JOptionPane.ERROR_MESSAGE);
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Comprar " + cantidad + " (" + tipoCompra + ") de " + proveedor.getNombreIngrediente() +
                        " por $" + costoTotal + "?",
                "Confirmar Compra", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            // 1. Actualizar dinero (Presupuesto)
            presupuesto.setPresupuesto(presupuesto.getPresupuesto() - costoTotal);
            presupuestoManager.actualizarPresupuesto(-costoTotal, 0, 0);

            // 2. Actualizar Stock
            actualizarStockInventario(proveedor.getNombreIngrediente(), cantidad, tipoCompra);

            // --- NUEVO: 3. REGISTRAR EN HISTORIAL ---
            ComprasManager comprasManager = new ComprasManager();
            Compra nuevaCompra = new Compra(
                    proveedor.getNombreIngrediente(),
                    cantidad,
                    tipoCompra,
                    costoTotal,
                    LocalDate.now()
            );
            comprasManager.registrarCompra(nuevaCompra);
            // ----------------------------------------

            actualizarLabelPresupuesto();
            JOptionPane.showMessageDialog(this, "Compra realizada y registrada con éxito.");
        }
    }

    private void actualizarStockInventario(String nombreIngrediente, int cantidad, String tipoCompra) {

        double cantidadAumentar = cantidad;
        if (tipoCompra.equals("Mayor")) cantidadAumentar = cantidad * 10;

        inventarioManager.actualizarStock(nombreIngrediente, cantidadAumentar);

    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Proveedor p : listaProveedores) {
            modeloTabla.addRow(new Object[]{
                    p.getNombreIngrediente(),
                    p.getCategoria(),
                    p.getPrecioPorUnidad(),
                    p.getPrecioPorKilo(),
                    p.getPrecioPorMayor(),
                    p.getFechaCaducidad()
            });
        }
    }

    private void actualizarLabelPresupuesto() {
        lblPresupuestoActual.setText("Presupuesto: $" + String.format("%.2f", presupuesto.getPresupuesto()));
    }

    public JPanel getPanelOrdenarCompra() {
        return panelPrincipal;
    }
}