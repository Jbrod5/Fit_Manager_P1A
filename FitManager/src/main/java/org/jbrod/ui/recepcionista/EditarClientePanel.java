package org.jbrod.ui.recepcionista;

import org.jbrod.controller.ClientesDB;
import org.jbrod.model.clientes_membresias.Cliente;
import org.jbrod.ui.recepcionista.RecepcionistaPanel;


import javax.swing.*;
import java.awt.*;

public class EditarClientePanel extends JPanel {

    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JButton btnGuardar;
    private JButton btnCancelar;


    private Cliente cliente;
    private ClientesDB clientesDB;
    private ClientesPanel clientesPanel;
    private RecepcionistaPanel recepcionistaPanel;

    public EditarClientePanel(RecepcionistaPanel recepcionistaPanel, ClientesPanel clientesPanel, Cliente cliente) {
        this.recepcionistaPanel = recepcionistaPanel;
        this.clientesPanel = clientesPanel;
        this.cliente = cliente;
        this.clientesDB = new ClientesDB();

        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Editar Cliente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        // === Formulario ===
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(cliente.getNombre());
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Correo:"));
        txtCorreo = new JTextField(cliente.getCorreo());
        formPanel.add(txtCorreo);

        add(formPanel, BorderLayout.CENTER);

        // === Botones ===
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar Cambios");
        btnCancelar = new JButton("Cancelar");

        botonesPanel.add(btnGuardar);
        botonesPanel.add(btnCancelar);

        add(botonesPanel, BorderLayout.SOUTH);

        // === Listeners ===
        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> cancelar());
    }

    private void guardarCambios() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, completa todos los campos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        cliente.setNombre(nombre);
        cliente.setCorreo(correo);

        boolean actualizado = clientesDB.actualizar(cliente);
        if (actualizado) {
            JOptionPane.showMessageDialog(this,
                    "Cliente actualizado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            clientesPanel.cargarClientes();

            // Volver al panel principal de clientes
            recepcionistaPanel.showInicioClientes();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar el cliente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        //CardLayout cl = (CardLayout) recepcionistaPanel.getContentPanel().getLayout();
        //cl.show(recepcionistaPanel.getContentPanel(), "Clientes");
        recepcionistaPanel.showInicioClientes();
    }
}
