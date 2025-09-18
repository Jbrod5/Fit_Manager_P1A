package org.jbrod.ui.Administrador;

import org.jbrod.controller.EmpleadoDB;
import org.jbrod.controller.InventarioDB;
import org.jbrod.model.empleados.Empleado;
import org.jbrod.model.empleados.RolEmpleado;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditarEmpleadoPanel extends JPanel {

    private EmpleadosPanel empleadosPanel;
    private AdministradorPanel administradorPanel;
    private EmpleadoDB empleadoDB;

    private Empleado empleado; // empleado a editar

    private JTextField txtNombre, txtCorreo, txtTelefono, txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<RolEmpleado> cbRol;
    private JComboBox<String> cbSucursal;
    private JButton btnGuardar, btnCancelar;

    public EditarEmpleadoPanel(AdministradorPanel administradorPanel, EmpleadosPanel empleadosPanel, Empleado empleado) {
        this.empleadosPanel = empleadosPanel;
        this.administradorPanel = administradorPanel;
        this.empleadoDB = new EmpleadoDB();
        this.empleado = empleado;

        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        txtNombre = new JTextField(empleado.getNombre());
        txtCorreo = new JTextField(empleado.getCorreo());
        txtTelefono = new JTextField(String.valueOf(empleado.getTelefono()));
        txtUsuario = new JTextField(empleado.getUsuario());
        txtPassword = new JPasswordField(""); // vacío: no cambia contraseña

        // --- Poblar roles desde la BD (objetos RolEmpleado) ---
        List<RolEmpleado> roles = empleadoDB.obtenerRoles();
        DefaultComboBoxModel<RolEmpleado> modeloRoles = new DefaultComboBoxModel<>();
        RolEmpleado seleccionado = null;
        for (RolEmpleado r : roles) {
            modeloRoles.addElement(r);
            if (r.getIdRolEmpleado() == empleado.getRolEmpleadoInt()) {
                seleccionado = r;
            }
        }
        cbRol = new JComboBox<>(modeloRoles);
        if (seleccionado != null) cbRol.setSelectedItem(seleccionado);

        // --- Poblar sucursales desde la BD ---
        List<String> sucursales = EmpleadoDB.obtenerNombresSucursales();
        DefaultComboBoxModel<String> modeloSuc = new DefaultComboBoxModel<>();
        for (String s : sucursales) modeloSuc.addElement(s);
        cbSucursal = new JComboBox<>(modeloSuc);
        cbSucursal.setSelectedItem(empleado.getNombreSucursal());

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Correo:"));
        formPanel.add(txtCorreo);

        formPanel.add(new JLabel("Teléfono:"));
        formPanel.add(txtTelefono);

        formPanel.add(new JLabel("Usuario:"));
        formPanel.add(txtUsuario);

        formPanel.add(new JLabel("Contraseña (solo si se cambia):"));
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Rol:"));
        formPanel.add(cbRol);

        formPanel.add(new JLabel("Sucursal:"));
        formPanel.add(cbSucursal);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> actualizarEmpleado());
        btnCancelar.addActionListener(e -> volverEmpleados());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void actualizarEmpleado() {
        // Validaciones básicas
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String tel = txtTelefono.getText().trim();
        String usuario = txtUsuario.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || tel.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos Nombre, Correo, Teléfono y Usuario son obligatorios.");
            return;
        }

        int telefono;
        try {
            telefono = Integer.parseInt(tel);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Teléfono inválido.");
            return;
        }

        // Actualizamos el objeto empleado con los valores del formulario
        empleado.setNombre(nombre);
        empleado.setCorreo(correo);
        empleado.setTelefono(telefono);
        empleado.setUsuario(usuario);

        // rol: obtener el id desde el objeto seleccionado
        RolEmpleado rolSel = (RolEmpleado) cbRol.getSelectedItem();
        if (rolSel != null) {
            empleado.setRolEmpleadoInt(rolSel.getIdRolEmpleado());
        }

        // sucursal: mapear nombre -> id
        String sucSeleccionada = (String) cbSucursal.getSelectedItem();
        int idSucursal = EmpleadoDB.obtenerIdSucursalPorNombre(sucSeleccionada);
        if (idSucursal > 0) {
            empleado.setId_sucursal(idSucursal); // asegúrate de tener este setter en Empleado
        }

        // contraseña solo si la cambió
        String nuevaPass = String.valueOf(txtPassword.getPassword()).trim();
        if (!nuevaPass.isEmpty()) {
            empleado.setPassword(nuevaPass);
        } else {
            // dejar contraseña actual en objeto para que EmpleadoDB sepa que no la debe cambiar
            empleado.setPassword(""); // o null — según cómo manejes la comprobación en actualizar()
        }

        boolean exito = empleadoDB.actualizar(empleado);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Empleado actualizado correctamente");
            volverEmpleados();
            empleadosPanel.cargarEmpleados(); // refrescar tabla
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar empleado");
        }
    }

    private void volverEmpleados() {
        administradorPanel.showInicioEmpleados();
    }
}
