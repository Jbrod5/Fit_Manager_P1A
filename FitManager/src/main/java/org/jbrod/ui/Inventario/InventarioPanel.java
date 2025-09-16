package org.jbrod.ui.Inventario;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.ui.VentanaPrincipal;

import javax.swing.*;

public class InventarioPanel extends JPanel {

    private Empleado emp;
    private VentanaPrincipal ventanaPrincipal;

    public InventarioPanel(Empleado emp, VentanaPrincipal ventanaPrincipal){
        this.emp = emp;
        this.ventanaPrincipal = ventanaPrincipal;
    }
}
