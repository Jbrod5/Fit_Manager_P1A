package org.jbrod.ui.Administrador;

import org.jbrod.controller.EmpleadoDB;
import org.jbrod.model.empleados.Empleado;
import org.jbrod.model.empleados.RolEmpleadoEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NuevoEmpleadoPanel extends JPanel {

    private JTextField txtNombre, txtCorreo, txtTelefono, txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol, comboSucursal;
    private JButton btnGuardar, btnCancelar;

    private EmpleadoDB empleadoDB;
    private AdministradorPanel administradorPanel;
    private EmpleadosPanel empleadosPanel;

    public NuevoEmpleadoPanel(AdministradorPanel administradorPanel, EmpleadosPanel empleadosPanel) {
        this.administradorPanel = administradorPanel;
        this.empleadosPanel = empleadosPanel;
        empleadoDB = new EmpleadoDB();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campos del formulario ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1; add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Correo:"), gbc);
        txtCorreo = new JTextField(20);
        gbc.gridx = 1; add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Teléfono:"), gbc);
        txtTelefono = new JTextField(20);
        gbc.gridx = 1; add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Usuario:"), gbc);
        txtUsuario = new JTextField(20);
        gbc.gridx = 1; add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1; add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Rol:"), gbc);
        comboRol = new JComboBox<>();
        for (RolEmpleadoEnum rol : RolEmpleadoEnum.values()) {
            if (rol != RolEmpleadoEnum.ADMINISTRADOR) comboRol.addItem(rol.name());
        }
        gbc.gridx = 1; add(comboRol, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Sucursal:"), gbc);
        comboSucursal = new JComboBox<>();
        comboSucursal.addItem("Central");
        comboSucursal.addItem("Norte");
        comboSucursal.addItem("Sur");
        gbc.gridx = 1; add(comboSucursal, gbc);

        // --- Botones ---
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, gbc);

        // --- Acciones de botones ---
        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnCancelar.addActionListener(e -> volverEmpleados());
    }

    private void guardarEmpleado() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefonoStr = txtTelefono.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String sucursalNombre = comboSucursal.getSelectedItem().toString();
        RolEmpleadoEnum rol = RolEmpleadoEnum.valueOf(comboRol.getSelectedItem().toString());

        // --- Validaciones ---
        if(nombre.isEmpty() || correo.isEmpty() || telefonoStr.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }
        if(!correo.contains("@")) {
            JOptionPane.showMessageDialog(this, "Correo inválido");
            return;
        }

        int telefono;
        try {
            telefono = Integer.parseInt(telefonoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Teléfono inválido");
            return;
        }

        // --- Crear empleado ---
        Empleado emp = new Empleado(
                0, // id generado en DB
                sucursalNombreToId(sucursalNombre),
                nombre,
                correo,
                telefono,
                rol.getId(),
                usuario,
                password
        );

        boolean exito = empleadoDB.insertar(emp);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Empleado registrado correctamente");
            limpiarCampos();
            volverEmpleados();
            empleadosPanel.cargarEmpleados(); // refrescar tabla
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar empleado");
        }
    }

    private int sucursalNombreToId(String nombre) {
        switch (nombre) {
            case "Central": return 1;
            case "Norte": return 2;
            case "Sur": return 3;
            default: return 0;
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
        comboSucursal.setSelectedIndex(0);
    }

    private void volverEmpleados() {
        CardLayout cl = (CardLayout) administradorPanel.getContentPanel().getLayout();
        cl.show(administradorPanel.getContentPanel(), "Empleados");
    }
}
