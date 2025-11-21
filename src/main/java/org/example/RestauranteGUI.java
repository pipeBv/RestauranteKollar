package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class RestauranteGUI extends JFrame {

    private InventarioManager inventarioManager;
    private PlatoManager platoManager;


    private DefaultTableModel modeloTablaInventario;
    private DefaultTableModel modeloTablaPlatos;

    private JTextField txtUsuario;
    private JPasswordField txtPass;
    private Usuario gerente = new Usuario("Benjamin", "1234");

    public RestauranteGUI() {

        inventarioManager = new InventarioManager();
        platoManager = new PlatoManager();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mostrarLogin();
    }

    private void mostrarLogin() {
        setTitle("Login - Restaurante Kollar");
        setSize(350, 200);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JPanel pnlUser = new JPanel();
        pnlUser.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField(15);
        pnlUser.add(txtUsuario);

        JPanel pnlPass = new JPanel();
        pnlPass.add(new JLabel("Contraseña:"));
        txtPass = new JPasswordField(15);
        pnlPass.add(txtPass);

        JButton btnLogin = new JButton("Iniciar Sesión");
        JButton btnSalir = new JButton("Salir");

        loginPanel.add(new JLabel("Bienvenido al Sistema", SwingConstants.CENTER));
        loginPanel.add(pnlUser);
        loginPanel.add(pnlPass);

        JPanel pnlBotones = new JPanel();
        pnlBotones.add(btnLogin);
        pnlBotones.add(btnSalir);
        loginPanel.add(pnlBotones);

        btnLogin.addActionListener(e -> validarLogin());
        btnSalir.addActionListener(e -> System.exit(0));

        setContentPane(loginPanel);
    }

    private void validarLogin() {
        String usuarioIngresado = txtUsuario.getText();
        String passIngresado = new String(txtPass.getPassword());
        if (usuarioIngresado.equals(gerente.getNombreUsuario()) &&
                passIngresado.equals(gerente.getContraseña())) {
            JOptionPane.showMessageDialog(this, "Bienvenido: " + usuarioIngresado);
            cargarInterfazPrincipal();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error: ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarInterfazPrincipal() {
        setTitle("Gestión de Restaurante - Panel Principal");
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Adminstrar Inventario", crearPanelInventario());
        tabbedPane.addTab("Administrar Platos", crearPanelPlatos());
        tabbedPane.addTab("Hacer Pedido", crearPanelPedidos());
        tabbedPane.addTab("Ver reporte", crearPanelReporte());
        tabbedPane.addTab("Administrar Empleados", crearPanelEmpleados());

        setContentPane(tabbedPane);
        revalidate();
        repaint();
    }


    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();


        tabbedPane.addTab("Adminstrar Inventario", crearPanelInventario());


        tabbedPane.addTab("Administrar Platos", crearPanelPlatos());

        tabbedPane.addTab("Hacer Pedido", crearPanelPedidos());

        tabbedPane.addTab("Ver reporte",crearPanelReporte());

        tabbedPane.addTab("Administrar Empleados",crearPanelEmpleados());

        add(tabbedPane);
        return panel;
    }


    private JPanel crearPanelPlatos() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] col = {"ID", "Nombre", "Precio"};
        modeloTablaPlatos = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPlatos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        JTextField txtNombre = new JTextField(10);
        JTextField txtPrecio = new JTextField(5);
        JButton btnAgregar = new JButton("Agregar Plato");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        panelBotones.add(new JLabel("Plato:"));
        panelBotones.add(txtNombre);
        panelBotones.add(new JLabel("Precio:"));
        panelBotones.add(txtPrecio);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        btnAgregar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText();
                double precio = Double.parseDouble(txtPrecio.getText());

                platoManager.agregarPlato(nombre, precio);
                actualizarTablaPlatos();

                txtNombre.setText("");
                txtPrecio.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido");
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String idHex = (String) modeloTablaPlatos.getValueAt(fila, 0);
                if(platoManager.eliminarPlato(idHex)){
                    actualizarTablaPlatos();
                }
            }
        });

        return panel;
    }

    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea areaPedido = new JTextArea("...");
        areaPedido.setEditable(false);
        panel.add(new JScrollPane(areaPedido), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        JButton btnNuevoPedido = new JButton("Nuevo Pedido");
        btnNuevoPedido.addActionListener(e -> {
            System.out.println("Nuevo pedido");
        });
        panelBotones.add(btnNuevoPedido);
        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private void actualizarTablaInventario() {
        modeloTablaInventario.setRowCount(0);
        ArrayList<ItemInventario> lista = inventarioManager.obtenerItems();
        for (ItemInventario item : lista) {
            modeloTablaInventario.addRow(new Object[]{
                    item.getId().toHexString(),
                    item.getNombre(),
                    item.getStock()
            });
        }
    }
    private JPanel crearPanelReporte(){
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea areaReporte = new JTextArea("...");
        areaReporte.setEditable(false);
        panel.add(new JScrollPane(areaReporte), BorderLayout.CENTER);
        return panel;
    }
    private JPanel crearPanelEmpleados(){
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea areaReporte = new JTextArea("...");
        areaReporte.setEditable(false);
        panel.add(new JScrollPane(areaReporte), BorderLayout.CENTER);
        String[] col = {"ID", "Nombre", "Salario"};
        modeloTablaPlatos = new DefaultTableModel(col, 0);
        JTable tabla = new JTable(modeloTablaPlatos);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel panelBotones = new JPanel();
        JTextField txtNombre = new JTextField(10);
        JTextField txtSalario = new JTextField(5);
        JTextField txtHorario = new JTextField(5);
        JButton btnAgregar = new JButton("Agregar Empleado");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        panelBotones.add(new JLabel("Empleado:"));
        return panel;
    }
    private void actualizarTablaPlatos() {
        modeloTablaPlatos.setRowCount(0);
        ArrayList<Plato> lista = platoManager.obtenerPlatos();
        for (Plato plato : lista) {
            modeloTablaPlatos.addRow(new Object[]{
                    plato.getId().toHexString(),
                    plato.getNombre(),
                    plato.getPrecio()
            });
        }
    }
}