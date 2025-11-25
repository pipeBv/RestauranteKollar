package org.example.aplicacion;

import javax.swing.*;
import java.awt.*;

import org.example.entidades.Gerente;
import org.example.gestion.EmpleadoManager;
import org.example.gestion.InventarioManager;
import org.example.gestion.MermaManager;
import org.example.gestion.PlatoManager;
import org.example.entidades.Empleado;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;

public class RestauranteGUI extends JFrame {
    Gerente gerente = new Gerente(1,"pipe","1234");
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
        if (nombreUsuario.equals(gerente.getNombre())) {
            if(contraseña.equals(gerente.getContraseña())) {
                System.out.println("Bienvenido Gerente: " + " " + gerente.getNombre());
                MenuGerente menuGerente = new MenuGerente(inventarioManager, platoManager, mermaManager, empleadoManager);
                menuGerente.setVisible(true);
                this.dispose();
                return;
            }
            else{
                System.out.println("Contraseña incorrecta");
            }
            }
        else if(nombreUsuario.equalsIgnoreCase(gerente.getNombre())){
            System.out.println("Usuario no encontrado");
        }
        List<Empleado> empleados = empleadoManager.cargarEmpleados();
        Boolean usuarioEncontrado = false;
            for(Empleado emp : empleados){
                if (nombreUsuario.equals(emp.getNombre())) {
                    usuarioEncontrado = true;
                    if(contraseña.equals(emp.getContraseña())){
                        System.out.println("Bienvenido Empleado: " + emp.getNombre());
                    MenuEmpleado menuEmpleado = new MenuEmpleado(inventarioManager, platoManager);
                    menuEmpleado.setVisible(true);
                    this.dispose();
                    return;
                    }
                    else{
                        System.out.println("Contraseña incorrecta");
                    }
                } else {
                    System.out.println("Usuario no encontrado");
                }
            }

    }
}