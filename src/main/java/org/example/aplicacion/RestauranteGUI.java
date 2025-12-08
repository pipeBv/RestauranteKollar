package org.example.aplicacion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.example.entidades.Gerente;
import org.example.entidades.Empleado;
import org.example.gestion.*;


public class RestauranteGUI extends JFrame {

    private JPanel mainPanel;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private EmpleadoManager empleadoManager;
    private InventarioManager inventarioManager;
    private PlatoManager platoManager;
    private MermaManager mermaManager;

    public RestauranteGUI() {
        empleadoManager = new EmpleadoManager();
        inventarioManager = new InventarioManager();
        platoManager = new PlatoManager();
        mermaManager = new MermaManager();
        setTitle("Login Restaurante");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel userLabel = new JLabel("Usuario: ");
        userField = new JTextField();
        JLabel passLabel = new JLabel("Contraseña: ");
        passwordField = new JPasswordField();
        loginButton = new JButton("Iniciar Sesión");
        passwordField.addActionListener(e -> loginButton.doClick());

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = userField.getText();
                String password = new String(passwordField.getPassword());
                verificarUsuario(usuario, password);
            }
        });

        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passwordField);
        mainPanel.add(new JLabel(""));
        mainPanel.add(loginButton);
        setContentPane(mainPanel);
    }

    private void verificarUsuario(String nombreUsuario, String contraseña) {

        List<Gerente> gerentes = empleadoManager.cargarGerentes();
        for (Gerente g : gerentes) {
            if (g.getNombre().equals(nombreUsuario)) {
                if (g.getContrasenia().equals(contraseña)) {
                    JOptionPane.showMessageDialog(this, "Bienvenido Gerente: " + g.getNombre());
                    MenuGerente menuGerente = new MenuGerente(inventarioManager, platoManager, mermaManager, empleadoManager);
                    menuGerente.setVisible(true);
                    this.dispose();
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta");
                    return;
                }
            }
        }

        List<Empleado> empleados = empleadoManager.cargarEmpleados();
        for (Empleado emp : empleados) {
            if (emp.getNombre().equals(nombreUsuario)) {
                if (emp.getContrasenia().equals(contraseña)) {
                    if (emp.getRolEmpleado().equals("Cajero")) {
                        JOptionPane.showMessageDialog(this, "Bienvenido Cajero: " + emp.getNombre());
                        MenuEmpleado menuEmpleado = new MenuEmpleado(inventarioManager, platoManager);
                        menuEmpleado.setVisible(true);
                        this.dispose();
                        return;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No puedes ingresar porque no eres cajero (Rol: " + emp.getRolEmpleado() + ")",
                                "Acceso Denegado",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta");
                    return;
                }
            }
        }


        JOptionPane.showMessageDialog(this, "Usuario no encontrado");
    }
}