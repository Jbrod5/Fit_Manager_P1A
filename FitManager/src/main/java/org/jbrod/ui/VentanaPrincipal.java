package org.jbrod.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private JPanel panelPrincipal;
    private final String nombreVentana = "Fit manager";

    public VentanaPrincipal() {
        //Configuraciones de la ventana :3
        setTitle(nombreVentana);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        panelPrincipal = new JPanel(new BorderLayout()); // layout simple
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        add(panelPrincipal);

    }


    public void cambiarPanel(JPanel panel, String nombreActividad){
        panelPrincipal.removeAll();

        panelPrincipal.add(panel, BorderLayout.CENTER);
        panelPrincipal.revalidate();
        panelPrincipal.repaint();

        this.setTitle(nombreVentana + ": " + nombreActividad);
    }

}





