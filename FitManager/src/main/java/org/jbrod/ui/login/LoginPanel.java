package org.jbrod.ui.login;

import org.jbrod.controller.Autenticar;
import org.jbrod.model.empleados.Empleado;
import org.jbrod.model.empleados.RolEmpleadoEnum;
import org.jbrod.ui.Administrador.AdministradorPanel;
import org.jbrod.ui.Entrenador.EntrenadorPanel;
import org.jbrod.ui.Inventario.InventarioPanel;
import org.jbrod.ui.recepcionista.RecepcionistaPanel;
import org.jbrod.ui.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginPanel extends JPanel{


    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;

    private VentanaPrincipal ventanaPrincipal;

    public LoginPanel(VentanaPrincipal ventanaPrincipal) {

        this.ventanaPrincipal = ventanaPrincipal;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10); //margenes :3
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsuario = new JLabel("Usuario:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblUsuario, gbc);

        txtUsuario = new JTextField(15);
        gbc.gridx = 1;
        add(txtUsuario, gbc);

        JLabel lblContrasena = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblContrasena, gbc);

        txtContrasena = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtContrasena, gbc);

        btnLogin = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(btnLogin, gbc);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }



    private void login() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        Autenticar auth = new Autenticar();
        try {
            System.out.println("Usuario: " +  usuario);
            System.out.println("Pass: " + contrasena);
            Empleado emp = auth.login(usuario, contrasena);
            // Convertir el int a enum
            RolEmpleadoEnum rolEnum = RolEmpleadoEnum.fromId(emp.getRolEmpleadoInt());

            if (rolEnum != null) {
                switch (rolEnum) {
                    case ADMINISTRADOR:
                        ventanaPrincipal.cambiarPanel(new AdministradorPanel(emp, this.ventanaPrincipal), "Administrador");
                        break;
                    case ENTRENADOR:
                        ventanaPrincipal.cambiarPanel(new EntrenadorPanel(emp, this.ventanaPrincipal), "Entrenador");
                        break;
                    case RECEPCIONISTA:
                        ventanaPrincipal.cambiarPanel(new RecepcionistaPanel(emp, this.ventanaPrincipal), "Recepcionista");
                        break;
                    case INVENTARIO:
                        ventanaPrincipal.cambiarPanel(new InventarioPanel(emp, this.ventanaPrincipal), "Inventario");
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Rol no reconocido");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Rol inválido");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectarse con la base de datos");
        } catch (NullPointerException n){
            n.printStackTrace();
            JOptionPane.showMessageDialog(this, "Credenciales invalidas.");

        }
    }

}
