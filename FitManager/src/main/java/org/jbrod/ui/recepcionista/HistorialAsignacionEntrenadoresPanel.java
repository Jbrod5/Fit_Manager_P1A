package org.jbrod.ui.recepcionista;

import org.jbrod.controller.EntrenadorDB;
import org.jbrod.model.clientes_membresias.EntrenadorAsignacionHistorial;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialAsignacionEntrenadoresPanel extends JPanel {

    private JTextField txtBusqueda;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private List<EntrenadorAsignacionHistorial> listaHistorial;

    private RecepcionistaPanel recepcionistaPanel;

    public HistorialAsignacionEntrenadoresPanel(RecepcionistaPanel recepcionistaPanel) {

        this.recepcionistaPanel = recepcionistaPanel;
        setLayout(new BorderLayout(10,10));

        // === Panel superior con buscador ===
        JPanel panelSuperior = new JPanel(new BorderLayout(5,5));
        panelSuperior.add(new JLabel("Buscar por nombre o correo del cliente:"), BorderLayout.WEST);
        txtBusqueda = new JTextField();
        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        panelSuperior.add(txtBusqueda, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // === Tabla historial ===
        String[] columnas = {"ID Cliente","Nombre Cliente","Correo Cliente","ID Entrenador","Nombre Entrenador","Correo Entrenador","Fecha Asignación","Sucursal Entrenador"};

        modeloTabla = new DefaultTableModel(columnas,0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(25);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        // Cargar historial
        cargarHistorial();
    }

    public  void cargarHistorial() {
        listaHistorial = EntrenadorDB.obtenerHistorial("");
        actualizarTabla(listaHistorial);
    }

    private void actualizarTabla(List<EntrenadorAsignacionHistorial> historial) {
        modeloTabla.setRowCount(0);
        for(EntrenadorAsignacionHistorial h : historial) {
            modeloTabla.addRow(new Object[]{
                    h.getIdCliente(),
                    h.getNombreCliente(),
                    h.getCorreoCliente(),
                    h.getIdEntrenador(),
                    h.getNombreEntrenador(),
                    h.getCorreoEntrenador(),
                    h.getFechaAsignacion(),
                    h.getNombreSucursal()
            });

        }
    }

    private void filtrar() {
        String texto = txtBusqueda.getText().trim().toLowerCase();
        List<EntrenadorAsignacionHistorial> filtrados = listaHistorial.stream()
                .filter(h -> h.getNombreCliente().toLowerCase().contains(texto)
                        || h.getCorreoCliente().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        actualizarTabla(filtrados);
    }
}
