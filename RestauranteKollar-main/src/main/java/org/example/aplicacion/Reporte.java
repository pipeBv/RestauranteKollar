package org.example.aplicacion;

import org.example.entidades.Compra;
import org.example.entidades.Ingrediente;
import org.example.entidades.Merma;
import org.example.entidades.Plato;
import org.example.entidades.Presupuesto;
import org.example.gestion.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Reporte extends JFrame {


    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private MermaManager mermaManager;
    private PresupuestoManager presupuestoManager;
    private VentasManager ventasManager;
    private ComprasManager comprasManager;

    private JLabel lblPresupuestoVal;
    private JLabel lblGananciasVal;
    private JLabel lblPerdidasVal;
    private JLabel lblGastosVal;

    private DefaultTableModel modeloVentas;
    private DefaultTableModel modeloCompras;
    private DefaultTableModel modeloAlertas;
    private DefaultTableModel modeloMermas;

    private DecimalFormat df = new DecimalFormat("$ #,##0.00");

    public Reporte(InventarioManager inventarioManager, PlatoManager platoManager, MermaManager mermaManager, List<Plato> platosVendidosIniciales) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        this.mermaManager = mermaManager;
        this.presupuestoManager = new PresupuestoManager();
        this.ventasManager = new VentasManager();
        this.comprasManager = new ComprasManager();

        initUI();
        cargarDatos();
    }

    private void initUI() {
        setTitle("Reporte Gerencial - Restaurante Kollar");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelResumen = new JPanel(new GridLayout(1, 4, 10, 0));
        panelResumen.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelResumen.add(crearTarjeta("Dinero en Caja", Color.decode("#27AE60")));
        panelResumen.add(crearTarjeta("Ventas Totales", Color.decode("#2980B9")));
        panelResumen.add(crearTarjeta("Gastos (Compras)", Color.decode("#E67E22")));
        panelResumen.add(crearTarjeta("Pérdidas (Mermas)", Color.decode("#C0392B")));

        lblPresupuestoVal = (JLabel) ((JPanel) panelResumen.getComponent(0)).getComponent(1);
        lblGananciasVal   = (JLabel) ((JPanel) panelResumen.getComponent(1)).getComponent(1);
        lblGastosVal      = (JLabel) ((JPanel) panelResumen.getComponent(2)).getComponent(1);
        lblPerdidasVal    = (JLabel) ((JPanel) panelResumen.getComponent(3)).getComponent(1);

        add(panelResumen, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        modeloVentas = new DefaultTableModel(new String[]{"Plato Vendido", "Precio Venta"}, 0);
        tabbedPane.addTab("Historial Ventas", new JScrollPane(new JTable(modeloVentas)));

        modeloCompras = new DefaultTableModel(new String[]{"Ingrediente", "Cantidad", "Tipo", "Fecha", "Costo Total"}, 0);
        tabbedPane.addTab("Historial Compras", new JScrollPane(new JTable(modeloCompras)));

        modeloAlertas = new DefaultTableModel(new String[]{"Ingrediente", "Stock Actual", "Estado"}, 0);
        JTable tablaAlertas = new JTable(modeloAlertas);
        tablaAlertas.setForeground(Color.RED);
        tabbedPane.addTab("Alertas Stock", new JScrollPane(tablaAlertas));

        modeloMermas = new DefaultTableModel(new String[]{"Ingrediente", "Cantidad", "Motivo", "Fecha"}, 0);
        JTable tablaMermas = new JTable(modeloMermas);
        tablaMermas.getColumnModel().getColumn(2).setPreferredWidth(300);
        tabbedPane.addTab("Mermas Registradas", new JScrollPane(tablaMermas));

        add(tabbedPane, BorderLayout.CENTER);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar Datos");
        btnActualizar.addActionListener(e -> cargarDatos());
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearTarjeta(String titulo, Color colorTexto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitulo.setForeground(Color.DARK_GRAY);

        JLabel lblValor = new JLabel("$ 0.00", SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 20));
        lblValor.setForeground(colorTexto);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        return panel;
    }

    private void cargarDatos() {
        Presupuesto presupuestoData = presupuestoManager.cargarPresupuesto();
        List<Compra> listaCompras = comprasManager.cargarCompras();

        double totalGastadoCompras = 0;
        for (Compra c : listaCompras) {
            totalGastadoCompras += c.getTotalGastado();
        }

        lblPresupuestoVal.setText(df.format(presupuestoData.getPresupuesto()));
        lblGananciasVal.setText(df.format(presupuestoData.getGanancias()));
        lblPerdidasVal.setText(df.format(presupuestoData.getPerdidas()));
        lblGastosVal.setText(df.format(totalGastadoCompras));

        modeloVentas.setRowCount(0);
        List<Plato> ventas = ventasManager.cargarVentasReporte();
        for (int i = ventas.size() - 1; i >= 0; i--) {
            Plato p = ventas.get(i);
            modeloVentas.addRow(new Object[]{p.getNombre(), df.format(p.getPrecio())});
        }

        modeloCompras.setRowCount(0);
        for (int i = listaCompras.size() - 1; i >= 0; i--) {
            Compra c = listaCompras.get(i);
            modeloCompras.addRow(new Object[]{
                    c.getIngrediente(), c.getCantidad(), c.getTipoCompra(), c.getFecha(), df.format(c.getTotalGastado())
            });
        }

        modeloAlertas.setRowCount(0);
        List<Ingrediente> inventario = inventarioManager.cargarIngredientes();
        boolean todoOk = true;
        for (Ingrediente ing : inventario) {
            if (ing.getStock() < 10.0) {
                modeloAlertas.addRow(new Object[]{ing.getNombre(), ing.getStock() + " " + ing.getUnidadMedida(), "CRÍTICO: STOCK BAJO"});
                todoOk = false;
            }
        }
        if (todoOk) modeloAlertas.addRow(new Object[]{"-", "-", "Inventario Óptimo"});

        modeloMermas.setRowCount(0);
        List<Merma> mermasTodas = mermaManager.cargarMermas();

        List<Merma> mermasValidas = mermasTodas.stream()
                .filter(m -> m.getIngrediente() != null &&
                        m.getIngrediente().getNombre() != null &&
                        !m.getIngrediente().getNombre().trim().isEmpty())
                .sorted(Comparator.comparing(Merma::getFecha).reversed())
                .collect(Collectors.toList());

        for (Merma m : mermasValidas) {
            modeloMermas.addRow(new Object[]{
                    m.getIngrediente().getNombre(),
                    m.getCantidad(),
                    m.getMotivo(),
                    m.getFecha()
            });
        }
    }
}