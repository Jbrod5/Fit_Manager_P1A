package org.jbrod.ui.recepcionista;

import org.jbrod.controller.PagosDB;

import javax.swing.*;
import java.awt.*;

public class CrearPagoPanel extends JPanel {

    private JLabel lblInfoCliente;
    private JTextField txtTipoServicio;
    private JTextArea txtDescripcion;
    private JTextField txtMonto;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private int idCliente;
    private String nombreCliente;
    private String correoCliente;
    private RecepcionistaPanel recepcionistaPanel;

    public CrearPagoPanel(RecepcionistaPanel recepcionistaPanel, int idCliente, String nombreCliente, String correoCliente) {
        this.recepcionistaPanel = recepcionistaPanel;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;

        setLayout(new BorderLayout(10, 10));

        // === Info cliente ===
        lblInfoCliente = new JLabel("Crear pago para: " + nombreCliente + " (" + correoCliente + ")");
        lblInfoCliente.setFont(new Font("Arial", Font.BOLD, 14));
        add(lblInfoCliente, BorderLayout.NORTH);

        // === Formulario central ===
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tipo servicio
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Tipo de servicio:"), gbc);
        txtTipoServicio = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        panelForm.add(txtTipoServicio, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelForm.add(new JLabel("Descripción:"), gbc);
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(scrollDesc, gbc);

        // Monto
        gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;
        panelForm.add(new JLabel("Monto:"), gbc);
        txtMonto = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(txtMonto, gbc);

        add(panelForm, BorderLayout.CENTER);

        // === Botones ===
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar pago");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        // === Listeners ===
        btnGuardar.addActionListener(e -> guardarPago());
        btnCancelar.addActionListener(e -> recepcionistaPanel.showInicioClientes());
    }

    private void guardarPago() {
        String tipoServicio = txtTipoServicio.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String montoStr = txtMonto.getText().trim();

        if (tipoServicio.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar al menos el tipo de servicio y el monto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean exito = PagosDB.registrarPago(idCliente, null, monto, tipoServicio, descripcion);

        if (exito) {
            JOptionPane.showMessageDialog(this, "Pago registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            recepcionistaPanel.showPagos(); // Volvemos al panel de pagos
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el pago.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
