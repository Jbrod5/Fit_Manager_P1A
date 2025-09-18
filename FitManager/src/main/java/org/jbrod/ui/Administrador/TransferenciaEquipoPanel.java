package org.jbrod.ui.Administrador;

import org.jbrod.controller.InventarioDB;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferenciaEquipoPanel extends JPanel {

    private AdministradorPanel administradorPanel;

    private JComboBox<String> comboEquipos;
    private JComboBox<String> comboDesde;
    private JComboBox<String> comboHacia;
    private JTextField txtCantidad;

    private  InventarioPanel inventarioPanel;

    public TransferenciaEquipoPanel(AdministradorPanel administradorPanel, InventarioPanel inventarioPanel) {
        this.administradorPanel = administradorPanel;
        this.inventarioPanel = inventarioPanel;


        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblEquipo = new JLabel("Equipo:");
        JLabel lblDesde = new JLabel("Desde sucursal:");
        JLabel lblHacia = new JLabel("Hacia sucursal:");
        JLabel lblCantidad = new JLabel("Cantidad:");

        comboEquipos = new JComboBox<>();
        comboDesde = new JComboBox<>();
        comboHacia = new JComboBox<>();
        txtCantidad = new JTextField(10);

        JButton btnTransferir = new JButton("Transferir");
        JButton btnVolver = new JButton("Cancelar");

        // Agregar componentes
        gbc.gridx = 0; gbc.gridy = 0; add(lblEquipo, gbc);
        gbc.gridx = 1; add(comboEquipos, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(lblDesde, gbc);
        gbc.gridx = 1; add(comboDesde, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(lblHacia, gbc);
        gbc.gridx = 1; add(comboHacia, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(lblCantidad, gbc);
        gbc.gridx = 1; add(txtCantidad, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnTransferir);
        btnPanel.add(btnVolver);
        add(btnPanel, gbc);

        // Cargar combos
        cargarDatos();

        // Acción transferir
        btnTransferir.addActionListener(e -> {
            String equipo = (String) comboEquipos.getSelectedItem();
            String desde = (String) comboDesde.getSelectedItem();
            String hacia = (String) comboHacia.getSelectedItem();
            int cantidad = Integer.parseInt(txtCantidad.getText());

            boolean ok = InventarioDB.transferirEquipo(equipo, desde, hacia, cantidad);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Transferencia realizada con éxito");
                inventarioPanel.cargarDatos();
                administradorPanel.showInicioInventario();
            } else {
                JOptionPane.showMessageDialog(this, "Error en la transferencia", "Error", JOptionPane.ERROR_MESSAGE);
                inventarioPanel.cargarDatos();
                administradorPanel.showInicioInventario();
            }
        });

        // Acción volver
        btnVolver.addActionListener(e -> {inventarioPanel.cargarDatos(); inventarioPanel.cargarDatos(); administradorPanel.showInicioInventario();});
    }

    private void cargarDatos() {
        // Equipos
        List<String> equipos = InventarioDB.obtenerNombresEquipos();
        for (String eq : equipos) {
            comboEquipos.addItem(eq);
        }

        // Sucursales
        List<String> sucursales = InventarioDB.obtenerNombresSucursales();
        for (String suc : sucursales) {
            comboDesde.addItem(suc);
            comboHacia.addItem(suc);
        }
    }
}
