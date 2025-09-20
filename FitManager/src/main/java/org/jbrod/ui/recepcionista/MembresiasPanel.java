package org.jbrod.ui.recepcionista;

import org.jbrod.controller.MembresiasDB;
import org.jbrod.model.clientes_membresias.MembresiaCliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MembresiasPanel extends JPanel {

    private JTable tablaMembresias;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JButton btnRegistrarMembresia;
    private JButton btnHistorial;

    private MembresiasDB membresiasDB;
    private RecepcionistaPanel recepcionistaPanel;

    public MembresiasPanel(RecepcionistaPanel recepcionistaPanel) {
        this.recepcionistaPanel = recepcionistaPanel;
        this.membresiasDB = new MembresiasDB();

        setLayout(new BorderLayout(10, 10));

        // === Panel superior: búsqueda ===
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.add(new JLabel("Buscar por nombre o correo:"), BorderLayout.WEST);

        txtBusqueda = new JTextField();
        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        panelSuperior.add(txtBusqueda, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // === Tabla de membresías ===
        String[] columnas = {"ID Cliente", "Nombre", "Fecha Inicio", "Fecha Fin", "Tipo Membresía", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaMembresias = new JTable(modeloTabla);
        tablaMembresias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaMembresias), BorderLayout.CENTER);

        // === Panel inferior con botones ===
        JPanel panelBotones = new JPanel(new BorderLayout());
        btnRegistrarMembresia = new JButton("Registrar Membresía");
        btnHistorial = new JButton("Historial de Membresías");

        panelBotones.add(btnRegistrarMembresia, BorderLayout.WEST);
        panelBotones.add(btnHistorial, BorderLayout.EAST);
        add(panelBotones, BorderLayout.SOUTH);

        cargarMembresias();

        // === Listener registrar membresía ===
        btnRegistrarMembresia.addActionListener(e -> registrarMembresia());

        // === Listener historial ===
        btnHistorial.addActionListener(e -> {
            HistorialMembresiasPanel panel = new HistorialMembresiasPanel(recepcionistaPanel);
            recepcionistaPanel.addView("HistorialMembresias", panel);
        });

    }

    public void cargarMembresias() {
        List<MembresiaCliente> membresias = membresiasDB.obtenerUltimasMembresiasTodosClientes();
        actualizarTabla(membresias);
    }

    private void actualizarTabla(List<MembresiaCliente> membresias) {
        modeloTabla.setRowCount(0);
        for (MembresiaCliente m : membresias) {
            modeloTabla.addRow(new Object[]{
                    m.getIdCliente(),
                    m.getNombreCliente(),
                    m.getFechaInicio(),
                    m.getFechaFin(),
                    m.getNombreTipoMembresia(),
                    m.isActiva() ? "Activa" : "No activa"
            });
        }
    }

    private void filtrar() {
        String texto = txtBusqueda.getText().trim().toLowerCase();
        List<MembresiaCliente> filtrados = membresiasDB.obtenerUltimasMembresiasTodosClientes().stream()
                .filter(m -> m.getNombreCliente().toLowerCase().contains(texto)
                        || m.getCorreoCliente().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        actualizarTabla(filtrados);
    }

    private void registrarMembresia() {
        int fila = tablaMembresias.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente primero.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estado = (String) modeloTabla.getValueAt(fila, 5); // columna "Estado"
        if ("Activa".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Este cliente ya tiene una membresía activa.\nNo puedes registrar otra hasta que expire.",
                    "Membresía activa",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = (int) modeloTabla.getValueAt(fila, 0);
        String nombreCliente = (String) modeloTabla.getValueAt(fila, 1);

        RegistrarMembresiaPanel panel = new RegistrarMembresiaPanel(idCliente, nombreCliente, this, recepcionistaPanel);
        recepcionistaPanel.addView("RegistrarMembresia", panel);
    }

}
