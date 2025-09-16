package org.jbrod.ui.Recepcionista;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.ui.VentanaPrincipal;

import javax.swing.*;

public class RecepcionistaPanel extends JPanel {

    private Empleado emp;
    private VentanaPrincipal ventanaPrincipal;

    public RecepcionistaPanel(Empleado emp, VentanaPrincipal ventanaPrincipal){
        this.emp = emp;
        this.ventanaPrincipal = ventanaPrincipal;

    }


}
