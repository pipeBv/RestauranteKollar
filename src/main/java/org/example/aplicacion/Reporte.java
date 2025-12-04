package org.example.aplicacion;

import org.example.entidades.Ingrediente;
import org.example.entidades.Merma;
import org.example.entidades.Plato;
import org.example.entidades.Presupuesto; // Importar
import org.example.gestion.InventarioManager;
import org.example.gestion.MermaManager;
import org.example.gestion.PlatoManager;
import org.example.gestion.PresupuestoManager; // Importar

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Reporte extends JFrame {
    private JTextArea areaReporte;
    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private MermaManager mermaManager;
    private PresupuestoManager presupuestoManager; // Nuevo

    private Presupuesto presupuestoData; // Guardar objeto presupuesto
    private List<Plato> platosVendidos;

    public Reporte(InventarioManager inventarioManager, PlatoManager platoManager, MermaManager mermaManager, List<Plato> platosVendidos) {
        this.inventarioManager = inventarioManager;
        this.platoManager = platoManager;
        this.mermaManager = mermaManager;
        this.platosVendidos = platosVendidos;
        this.presupuestoManager = new PresupuestoManager(); // Inicializar

        setTitle("Reporte General del Restaurante");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inicializarComponentes();
        generarDatosReporte();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JLabel titulo = new JLabel("Reporte de Estado y Finanzas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaReporte.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(areaReporte);

        JButton btnCerrar = new JButton("Cerrar Reporte");
        btnCerrar.addActionListener(e -> this.dispose());

        panelPrincipal.add(titulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(btnCerrar, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private void generarDatosReporte() {
        StringBuilder reporte = new StringBuilder();

        // Cargar presupuesto real de la BD
        this.presupuestoData = presupuestoManager.cargarPresupuesto();

        reporte.append("====== REPORTE GENERAL ======\n\n");
        reporte.append("--- ESTADO FINANCIERO ---\n");
        reporte.append(String.format("Presupuesto Actual: $%.2f\n", presupuestoData.getPresupuesto()));
        reporte.append(String.format("Ganancias Totales: $%.2f\n", presupuestoData.getGanancias()));
        reporte.append(String.format("Pérdidas Totales: $%.2f\n", presupuestoData.getPerdidas()));
        reporte.append("\n");

        if (presupuestoData.getPerdidas() > presupuestoData.getGanancias()) {
            reporte.append("ALERTA: ESTAMOS TENIENDO MUCHAS PÉRDIDAS\n");
            reporte.append("Revisar gestión de inventario y mermas urgentemente\n");
        }
        reporte.append("\n");

        reporte.append("--- MERMAS ---\n");
        List<Merma> listaMermas = mermaManager.cargarMermas();
        if (listaMermas.isEmpty()) {
            reporte.append("No hay mermas registradas\n");
        } else {
            for (Merma m : listaMermas) {
                reporte.append(String.format("- %s (Motivo: %s) - Cantidad: %.2f\n",
                    m.getIngrediente().getNombre(), m.getMotivo(), m.getCantidad()));
            }
        }
        if (listaMermas.size() > 10) {
            reporte.append("ALERTA: Se están registrando muchas incidencias de merma\n");
        }
        reporte.append("\n");

        reporte.append("--- INGREDIENTES (STOCK) ---\n");
        List<Ingrediente> inventario = inventarioManager.cargarIngredientes();
        for (Ingrediente ing : inventario) {
            reporte.append(String.format("%s: %.2f %s\n", ing.getNombre(), ing.getStock(), ing.getUnidadMedida()));
        }
        reporte.append("\n");


        reporte.append("--- DISPONIBILIDAD DE PLATOS ---\n");

        List<Plato> menu = platoManager.cargarPlatos();
        boolean faltanIngredientesGeneral = false;

        for (Plato plato : menu) {
            if (inventarioManager.verificarStock(plato.getListaIngredientes(), 1)) {
                reporte.append("[DISPONIBLE] ").append(plato.getNombre()).append("\n");
            } else {
                reporte.append("(NO DISPONIBLE) ").append(plato.getNombre()).append(" - ");
                reporte.append("Faltan ingredientes\n");
                faltanIngredientesGeneral = true;
            }
        }
        if (faltanIngredientesGeneral) {
            reporte.append("AVISO: Faltan ingredientes para completar el menú\n");
        }
        reporte.append("\n");
        reporte.append("--- VENTAS SESIÓN ACTUAL (Info) ---\n");
        if (platosVendidos == null || platosVendidos.isEmpty()) {
            reporte.append("No se han registrado ventas en esta consulta.\n");
        } else {
            for (Plato p : platosVendidos) {
                reporte.append(" + ").append(p.getNombre()).append(" - $").append(p.getPrecio()).append("\n");
            }
            reporte.append(String.format("Total Platos Listados: %d\n", platosVendidos.size()));
        }
        areaReporte.setText(reporte.toString());
    }

}