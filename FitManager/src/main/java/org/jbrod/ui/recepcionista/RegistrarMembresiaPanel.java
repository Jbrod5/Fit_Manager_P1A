package org.jbrod.ui.recepcionista;

import org.jbrod.controller.MembresiasDB;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class RegistrarMembresiaPanel extends JPanel {

    private JComboBox<String> comboTipoMembresia;
    private JComboBox<String> comboDuracion;
    private JButton btnAceptar;
    private JButton btnCancelar;

    private int idCliente;
    private String nombreCliente;
    private MembresiasPanel membresiasPanel;
    private RecepcionistaPanel recepcionistaPanel;

    public RegistrarMembresiaPanel(int idCliente, String nombreCliente,
                                   MembresiasPanel membresiasPanel,
                                   RecepcionistaPanel recepcionistaPanel) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.membresiasPanel = membresiasPanel;
        this.recepcionistaPanel = recepcionistaPanel;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Título con el nombre del cliente ===
        JLabel lblTitulo = new JLabel("Registrar membresía para: " + nombreCliente);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        // === Tipo de membresía ===
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Tipo de Membresía:"), gbc);

        comboTipoMembresia = new JComboBox<>();
        cargarTiposMembresia();
        gbc.gridx = 1; gbc.gridy = 1;
        add(comboTipoMembresia, gbc);

        // === Duración ===
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Duración:"), gbc);

        comboDuracion = new JComboBox<>(new String[]{"1 mes", "3 meses", "12 meses"});
        gbc.gridx = 1; gbc.gridy = 2;
        add(comboDuracion, gbc);

        // === Botones ===
        btnAceptar = new JButton("Aceptar");
        btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(panelBotones, gbc);

        // === Listeners ===
        btnAceptar.addActionListener(e -> registrarMembresia());
        btnCancelar.addActionListener(e -> recepcionistaPanel.showMembresias());
    }

    private void cargarTiposMembresia() {
        List<String> tipos = MembresiasDB.obtenerTiposMembresia();
        for (String tipo : tipos) {
            comboTipoMembresia.addItem(tipo);
        }
    }

    private void registrarMembresia() {
        String tipoSeleccionado = (String) comboTipoMembresia.getSelectedItem();
        String duracion = (String) comboDuracion.getSelectedItem();

        if(tipoSeleccionado == null || duracion == null) {
            JOptionPane.showMessageDialog(this, "Selecciona tipo y duración.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idTipo = MembresiasDB.obtenerIdTipoMembresiaPorNombre(tipoSeleccionado);

        LocalDate inicio = LocalDate.now();
        LocalDate fin;
        switch(duracion) {
            case "1 mes": fin = inicio.plusMonths(1); break;
            case "3 meses": fin = inicio.plusMonths(3); break;
            case "12 meses": fin = inicio.plusYears(1); break;
            default: fin = inicio.plusMonths(1);
        }

        double monto = 100.0; // Aquí puedes calcular el monto según el tipo de membresía

        boolean exito = MembresiasDB.registrarMembresia(idCliente, idTipo, inicio, fin, monto);
        if(exito) {
            JOptionPane.showMessageDialog(this, "Membresía registrada con éxito.");
            membresiasPanel.cargarMembresias();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar membresía.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        recepcionistaPanel.showMembresias();
    }
}
