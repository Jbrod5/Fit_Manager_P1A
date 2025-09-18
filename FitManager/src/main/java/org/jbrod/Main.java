package org.jbrod;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.jbrod.ui.VentanaPrincipal;
import org.jbrod.ui.login.LoginPanel;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            //Establecer el tema FlatLaf Cupertino Light
            FlatMacLightLaf.setup();
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Crear la ventana principal y mostrarla
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            ventanaPrincipal.setVisible(true);

            // 🔹 Cargar el login al inicio
            LoginPanel loginPanel = new LoginPanel(ventanaPrincipal);
            ventanaPrincipal.cambiarPanel(loginPanel, "login");
        });
    }
}
