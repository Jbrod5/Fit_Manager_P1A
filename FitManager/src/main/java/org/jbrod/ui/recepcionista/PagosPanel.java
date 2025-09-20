package org.jbrod.ui.recepcionista;

import org.jbrod.controller.PagosDB;
import org.jbrod.model.pagos_asistencias.Pago;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PagosPanel extends JPanel {

    private JTable tablaPagos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusquedaNombreCorreo;
    private JTextField txtBusquedaServicio;
    private List<Pago> listaCompleta; // Guardamos todos los pagos

    public PagosPanel() {
        setLayout(new BorderLayout(10, 10));

        // === Panel superior con dos buscadores ===
        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 5, 5));
        panelSuperior.add(new JLabel("Buscar por nombre o correo:"));
        txtBusquedaNombreCorreo = new JTextField();
        panelSuperior.add(txtBusquedaNombreCorreo);

        panelSuperior.add(new JLabel("Buscar por tipo de servicio:"));
        txtBusquedaServicio = new JTextField();
        panelSuperior.add(txtBusquedaServicio);

        // Listeners para filtrar en ambos campos
        txtBusquedaNombreCorreo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        txtBusquedaServicio.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        add(panelSuperior, BorderLayout.NORTH);

        // === Tabla de pagos ===
        String[] columnas = {"ID Pago", "Cliente", "Correo", "Monto", "Fecha", "Servicio", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaPagos = new JTable(modeloTabla);
        tablaPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaPagos);
        add(scroll, BorderLayout.CENTER);

        // === Cargar pagos al inicio ===
        cargarPagos();
    }

    public void cargarPagos() {
        listaCompleta = PagosDB.obtenerPagos();
        actualizarTabla(listaCompleta);
    }

    private void actualizarTabla(List<Pago> pagos) {
        modeloTabla.setRowCount(0);
        for (Pago p : pagos) {
            modeloTabla.addRow(new Object[]{
                    p.getIdPago(),
                    p.getNombreCliente(),
                    p.getCorreoCliente(),
                    p.getMonto(),
                    p.getFechaPago(),
                    p.getTipoServicio(),
                    p.getDescripcion()
            });
        }
    }

    private void filtrar() {
        String textoNombreCorreo = txtBusquedaNombreCorreo.getText().trim().toLowerCase();
        String textoServicio = txtBusquedaServicio.getText().trim().toLowerCase();

        List<Pago> filtrados = listaCompleta.stream()
                .filter(p -> (textoNombreCorreo.isEmpty()
                        || p.getNombreCliente().toLowerCase().contains(textoNombreCorreo)
                        || p.getCorreoCliente().toLowerCase().contains(textoNombreCorreo)))
                .filter(p -> (textoServicio.isEmpty()
                        || p.getTipoServicio().toLowerCase().contains(textoServicio)))
                .collect(Collectors.toList());

        actualizarTabla(filtrados);
    }
}
