package org.jbrod;

import org.jbrod.ui.VentanaPrincipal;
import org.jbrod.ui.login.LoginPanel;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
        ventanaPrincipal.setVisible(true);

        //Lo primero que debe casrgarse es el login :3
        LoginPanel loginPanel = new LoginPanel(ventanaPrincipal);
        ventanaPrincipal.cambiarPanel(loginPanel,  "login");

    }
}