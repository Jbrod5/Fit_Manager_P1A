package org.jbrod.ui.Entrenador;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.ui.VentanaPrincipal;

import javax.swing.*;

public class EntrenadorPanel extends JPanel {

    private Empleado emp;
    private VentanaPrincipal ventanaPrincipal;

    public EntrenadorPanel(Empleado emp, VentanaPrincipal ventanaPrincipal){
        this.emp = emp;
        this.ventanaPrincipal = ventanaPrincipal;
    }


}
