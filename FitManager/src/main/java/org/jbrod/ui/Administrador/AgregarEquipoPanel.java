package org.jbrod.ui.Administrador;

import org.jbrod.controller.InventarioDB;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AgregarEquipoPanel extends JPanel {

    private AdministradorPanel administradorPanel;
    private InventarioPanel inventarioPanel;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtCantidad;
    private JComboBox<String> comboSucursales;

    private JButton btnCancelar;
    private JButton btnGuardar;

    public AgregarEquipoPanel(AdministradorPanel administradorPanel, InventarioPanel inventarioPanel) {
        this.administradorPanel = administradorPanel;
        this.inventarioPanel = inventarioPanel;

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNombre = new JLabel("Nombre del equipo:");
        txtNombre = new JTextField(20);

        JLabel lblDescripcion = new JLabel("Descripción:");
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);

        JLabel lblCantidad = new JLabel("Cantidad inicial:");
        txtCantidad = new JTextField(5);

        JLabel lblSucursal = new JLabel("Sucursal destino:");
        comboSucursales = new JComboBox<>();
        cargarSucursales();

        btnGuardar = new JButton("Guardar Equipo");
        btnCancelar = new JButton("Cancelar");

        // Colocar componentes en grid
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblDescripcion, gbc);
        gbc.gridx = 1; formPanel.add(scrollDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblCantidad, gbc);
        gbc.gridx = 1; formPanel.add(txtCantidad, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblSucursal, gbc);
        gbc.gridx = 1; formPanel.add(comboSucursales, gbc);

        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(btnGuardar, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Acciones
        btnGuardar.addActionListener(e -> guardarEquipo());
        btnCancelar.addActionListener(e -> administradorPanel.showInicioInventario());
    }

    private void cargarSucursales() {
        List<String> sucursales = InventarioDB.obtenerNombresSucursales();
        for (String s : sucursales) {
            comboSucursales.addItem(s);
        }
    }

    private void guardarEquipo() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        String sucursal = (String) comboSucursales.getSelectedItem();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean exito = InventarioDB.agregarEquipoEnSucursal(nombre, descripcion, sucursal, cantidad);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Equipo registrado con éxito");
            txtNombre.setText("");
            txtDescripcion.setText("");
            txtCantidad.setText("");

            inventarioPanel.cargarDatos();
            administradorPanel.showInicioInventario();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el equipo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
