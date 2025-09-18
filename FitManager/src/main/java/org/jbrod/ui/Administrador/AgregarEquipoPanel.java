package org.jbrod.ui.Administrador;

import org.jbrod.controller.InventarioDB;

import javax.swing.*;
import java.awt.*;

public class AgregarEquipoPanel extends JPanel {

    private AdministradorPanel administradorPanel;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;

    private JButton btnCancelar;
    private JButton btnGuardar;

    InventarioPanel inventarioPanel;

    public AgregarEquipoPanel(AdministradorPanel administradorPanel, InventarioPanel inventarioPanel) {
        this.administradorPanel = administradorPanel;
        this.inventarioPanel = inventarioPanel;

        setLayout(new BorderLayout());

        // 🔹 Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNombre = new JLabel("Nombre del equipo:");
        txtNombre = new JTextField(20);

        JLabel lblDescripcion = new JLabel("Descripción:");
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);

        btnGuardar = new JButton("Guardar Equipo");
        btnCancelar = new JButton("Cancelar");

        // 🔹 Colocar en grid
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        formPanel.add(scrollDescripcion, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(btnGuardar, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 🔹 Acción botón
        btnGuardar.addActionListener(e -> guardarEquipo());
        btnCancelar.addActionListener(e -> administradorPanel.showInicioInventario());



    }

    private void guardarEquipo() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean exito = InventarioDB.agregarEquipo(nombre, descripcion);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Equipo registrado con éxito");
            txtNombre.setText("");
            txtDescripcion.setText("");


            inventarioPanel.cargarDatos();
            administradorPanel.showInicioInventario();

        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el equipo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
