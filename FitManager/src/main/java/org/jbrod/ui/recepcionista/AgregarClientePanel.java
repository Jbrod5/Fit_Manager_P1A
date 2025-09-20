package org.jbrod.ui.recepcionista;

import org.jbrod.controller.ClientesDB;
import org.jbrod.model.clientes_membresias.Cliente;

import javax.swing.*;
import java.awt.*;

public class AgregarClientePanel extends JPanel {

    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private ClientesDB clientesDB;
    private ClientesPanel clientesPanel;
    private RecepcionistaPanel recepcionistaPanel;

    public AgregarClientePanel(RecepcionistaPanel recepcionistaPanel, ClientesPanel clientesPanel) {
        this.recepcionistaPanel = recepcionistaPanel;
        this.clientesPanel = clientesPanel;
        this.clientesDB = new ClientesDB();

        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Agregar Cliente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        // === Formulario ===
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        formPanel.add(txtCorreo);

        add(formPanel, BorderLayout.CENTER);

        // === Botones ===
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        botonesPanel.add(btnGuardar);
        botonesPanel.add(btnCancelar);

        add(botonesPanel, BorderLayout.SOUTH);

        // === Listeners ===
        btnGuardar.addActionListener(e -> guardarCliente());
        btnCancelar.addActionListener(e -> cancelar());
    }

    private void guardarCliente() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, completa todos los campos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente nuevo = new Cliente(0, nombre, correo, null); // id y fecha los genera la BD
        boolean insertado = clientesDB.insertar(nuevo);

        if (insertado) {
            JOptionPane.showMessageDialog(this,
                    "Cliente agregado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            clientesPanel.cargarClientes();

            recepcionistaPanel.showInicioClientes();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar el cliente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        recepcionistaPanel.showInicioClientes();
    }
}
