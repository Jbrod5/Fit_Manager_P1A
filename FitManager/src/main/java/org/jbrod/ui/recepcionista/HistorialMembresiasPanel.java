package org.jbrod.ui.recepcionista;

import org.jbrod.controller.MembresiasDB;
import org.jbrod.model.clientes_membresias.MembresiaCliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialMembresiasPanel extends JPanel {

    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JComboBox<String> comboFiltroTipo;
    private JButton btnVolver;

    private RecepcionistaPanel recepcionistaPanel;
    private List<MembresiaCliente> historialCompleto;

    public HistorialMembresiasPanel(RecepcionistaPanel recepcionistaPanel) {
        this.recepcionistaPanel = recepcionistaPanel;

        setLayout(new BorderLayout(10, 10));

        // === Panel superior: búsqueda y filtro ===
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));

        // Panel de filtros internos (grid para alinear ambos)
        JPanel panelFiltros = new JPanel(new GridLayout(1, 2, 5, 0));

        // Buscar por nombre/correo
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        panelBusqueda.add(new JLabel("Buscar (nombre/correo):"), BorderLayout.WEST);
        txtBusqueda = new JTextField();
        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        panelBusqueda.add(txtBusqueda, BorderLayout.CENTER);

        // Filtro por tipo de membresía
        JPanel panelTipo = new JPanel(new BorderLayout(5, 5));
        panelTipo.add(new JLabel("Tipo Membresía:"), BorderLayout.WEST);
        comboFiltroTipo = new JComboBox<>();
        comboFiltroTipo.addItem("Todos"); // opción general
        comboFiltroTipo.addItem("Basica");
        comboFiltroTipo.addItem("Premium");
        comboFiltroTipo.addItem("VIP");
        comboFiltroTipo.addActionListener(e -> filtrar());
        panelTipo.add(comboFiltroTipo, BorderLayout.CENTER);

        // Añadir ambos al panel de filtros
        panelFiltros.add(panelBusqueda);
        panelFiltros.add(panelTipo);

        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // === Tabla historial ===
        String[] columnas = {"Cliente", "Correo", "Fecha Inicio", "Fecha Fin", "Tipo Membresía"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        // === Botón volver ===
        btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> recepcionistaPanel.showMembresias());
        add(btnVolver, BorderLayout.SOUTH);

        cargarHistorial();
    }

    private void cargarHistorial() {
        historialCompleto = MembresiasDB.obtenerHistorialMembresiasTodos();
        actualizarTabla(historialCompleto);
    }

    private void actualizarTabla(List<MembresiaCliente> membresias) {
        modeloTabla.setRowCount(0);
        for (MembresiaCliente m : membresias) {
            modeloTabla.addRow(new Object[]{
                    m.getNombreCliente(),
                    m.getCorreoCliente(),
                    m.getFechaInicio(),
                    m.getFechaFin(),
                    m.getNombreTipoMembresia()
            });
        }
    }

    private void filtrar() {
        String texto = txtBusqueda.getText().trim().toLowerCase();
        String tipoSeleccionado = (String) comboFiltroTipo.getSelectedItem();

        List<MembresiaCliente> filtrados = historialCompleto.stream()
                .filter(m -> (m.getNombreCliente().toLowerCase().contains(texto)
                        || m.getCorreoCliente().toLowerCase().contains(texto)))
                .filter(m -> tipoSeleccionado.equals("Todos")
                        || m.getNombreTipoMembresia().equalsIgnoreCase(tipoSeleccionado))
                .collect(Collectors.toList());

        actualizarTabla(filtrados);
    }
}
