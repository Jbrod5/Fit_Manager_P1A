package org.jbrod.ui.recepcionista;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.ui.Administrador.EmpleadosPanel;
import org.jbrod.ui.Administrador.InventarioPanel;
import org.jbrod.ui.VentanaPrincipal;
import org.jbrod.ui.login.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RecepcionistaPanel extends JPanel {

    private Empleado emp;
    private VentanaPrincipal ventanaPrincipal;

    private JPanel contentPanel; // aquí irán las vistas dinámicas



    public RecepcionistaPanel(Empleado emp, VentanaPrincipal ventanaPrincipal){
        this.emp = emp;
        this.ventanaPrincipal = ventanaPrincipal;



        setLayout(new BorderLayout());

        // 🔹 Barra de navegación superior
        JPanel navBar = new JPanel(new BorderLayout());
        //navBar.setBackground(new Color(220, 220, 220));

        // 🔹 Panel con botones de navegación (lado izquierdo)
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnClientes = new JButton("Clientes");
        JButton btnMembresias = new JButton("Membresias");
        JButton btnAsistencias = new JButton("Asistencias");
        JButton btnPagos = new JButton("Pagos");

        navButtons.add(btnClientes);
        navButtons.add(btnMembresias);
        navButtons.add(btnAsistencias);
        navButtons.add(btnPagos);

        // 🔹 Botón de cerrar sesión (lado derecho)
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        navBar.add(navButtons, BorderLayout.WEST);
        navBar.add(btnCerrarSesion, BorderLayout.EAST);

        // 🔹 Contenedor central con CardLayout
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(new ClientesPanel(this), "Clientes");
        contentPanel.add(new JLabel("Bienvenido recepcionista: Membresias" + emp.getNombre(), SwingConstants.CENTER), "Membresias");
        contentPanel.add(new JLabel("Bienvenido recepcionista: Asistencias" + emp.getNombre(), SwingConstants.CENTER), "Asistencias");
        contentPanel.add(new JLabel("Bienvenido recepcionista: Pagos" + emp.getNombre(), SwingConstants.CENTER), "Pagos");
        //contentPanel.add(new EmpleadosPanel(this), "Empleados");
        //contentPanel.add(new InventarioPanel(this), "Inventario");

        // 🔹 Acciones de los botones
        btnClientes.addActionListener((ActionEvent e) -> showView("Clientes"));
        btnMembresias.addActionListener((ActionEvent e) -> showView("Membresias"));
        btnAsistencias.addActionListener((ActionEvent e) -> showView("Asistencias"));
        btnPagos.addActionListener((ActionEvent e) -> showView("Pagos"));

        btnCerrarSesion.addActionListener((ActionEvent e) -> {
            //JOptionPane.showMessageDialog(this, "Cerrando sesión...");
            ventanaPrincipal.cambiarPanel(new LoginPanel(ventanaPrincipal), "Login");

        });

        // 🔹 Agregar al panel principal
        add(navBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

    }


    public void showView(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    public void showInicioClientes(){
        showView("Clientes");
    }



    public void addView(String name, JPanel panel) {
        contentPanel.add(panel, name);
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);

    }

    public JPanel getContentPanel() {
        return contentPanel;
    }



}
