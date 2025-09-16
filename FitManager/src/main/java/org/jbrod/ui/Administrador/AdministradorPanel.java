package org.jbrod.ui.Administrador;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.ui.VentanaPrincipal;
import org.jbrod.ui.login.LoginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdministradorPanel extends JPanel {

    private Empleado emp;
    private VentanaPrincipal ventanaPrincipal;

    private JPanel contentPanel; // aquí irán las vistas dinámicas

    public AdministradorPanel(Empleado emp, VentanaPrincipal ventanaPrincipal) {
        this.emp = emp;
        this.ventanaPrincipal = ventanaPrincipal;

        setLayout(new BorderLayout());

        // 🔹 Barra de navegación superior
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(new Color(220, 220, 220));

        // 🔹 Panel con botones de navegación (lado izquierdo)
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDashboard = new JButton("Dashboard");
        JButton btnEmpleados = new JButton("Empleados");
        JButton btnInventario = new JButton("Inventario");

        navButtons.add(btnDashboard);
        navButtons.add(btnEmpleados);
        navButtons.add(btnInventario);

        // 🔹 Botón de cerrar sesión (lado derecho)
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        navBar.add(navButtons, BorderLayout.WEST);
        navBar.add(btnCerrarSesion, BorderLayout.EAST);

        // 🔹 Contenedor central con CardLayout
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(new JLabel("Bienvenido administrador " + emp.getNombre(),
                SwingConstants.CENTER), "Dashboard");
        contentPanel.add(new EmpleadosPanel(), "Empleados");
        contentPanel.add(new JLabel("Gestión de inventario aquí", SwingConstants.CENTER), "Inventario");

        // 🔹 Acciones de los botones
        btnDashboard.addActionListener((ActionEvent e) -> showView("Dashboard"));
        btnEmpleados.addActionListener((ActionEvent e) -> showView("Empleados"));
        btnInventario.addActionListener((ActionEvent e) -> showView("Inventario"));

        btnCerrarSesion.addActionListener((ActionEvent e) -> {
            //JOptionPane.showMessageDialog(this, "Cerrando sesión...");
            ventanaPrincipal.cambiarPanel(new LoginPanel(ventanaPrincipal), "Login");

        });

        // 🔹 Agregar al panel principal
        add(navBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void showView(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }
}
