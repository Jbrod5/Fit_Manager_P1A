package org.jbrod.ui.recepcionista;



import org.jbrod.controller.AsistenciaDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AsistenciasPanel extends JPanel {

    private JTable tablaAsistencias;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscarCorreo;
    private JButton btnBuscar, btnRegistrar, btnRefrescar;

    private RecepcionistaPanel recepcionistaPanel;

    public AsistenciasPanel(RecepcionistaPanel recepcionistaPanel) {
        this.recepcionistaPanel = recepcionistaPanel;

        setLayout(new BorderLayout());

        // 🔹 Panel superior con opciones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtBuscarCorreo = new JTextField(20);
        btnBuscar = new JButton("Buscar por correo");
        btnRegistrar = new JButton("Registrar asistencia");
        btnRefrescar = new JButton("Refrescar");

        topPanel.add(new JLabel("Correo:"));
        topPanel.add(txtBuscarCorreo);
        topPanel.add(btnBuscar);
        topPanel.add(btnRegistrar);
        topPanel.add(btnRefrescar);

        add(topPanel, BorderLayout.NORTH);

        // 🔹 Tabla asistencias
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Cliente", "Correo", "Sucursal", "Entrada"}, 0);
        tablaAsistencias = new JTable(modeloTabla);

        JScrollPane scrollPane = new JScrollPane(tablaAsistencias);
        add(scrollPane, BorderLayout.CENTER);

        // 🔹 Cargar datos iniciales
        cargarAsistencias();

        // 🔹 Acción buscar
        btnBuscar.addActionListener(e -> {
            String correo = txtBuscarCorreo.getText().trim();
            if (!correo.isEmpty()) {
                cargarAsistenciasPorCorreo(correo);
            } else {
                JOptionPane.showMessageDialog(this, "Ingrese un correo para buscar", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 🔹 Acción registrar
        btnRegistrar.addActionListener(e -> registrarAsistencia());

        // 🔹 Acción refrescar
        btnRefrescar.addActionListener(e -> cargarAsistencias());
    }

    // Metodos auxiliaressss

    private void cargarAsistencias() {
        modeloTabla.setRowCount(0);
        List<Object[]> lista = AsistenciaDB.getInstance().obtenerAsistencias();
        for (Object[] fila : lista) {
            modeloTabla.addRow(fila);
        }
    }

    private void cargarAsistenciasPorCorreo(String correo) {
        modeloTabla.setRowCount(0);
        List<Object[]> lista = AsistenciaDB.getInstance().obtenerAsistenciasPorCorreo(correo);
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron asistencias para: " + correo, "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
        for (Object[] fila : lista) {
            modeloTabla.addRow(fila);
        }
    }

    private void registrarAsistencia() {

        RegistrarAsistenciaPanel registrarAsistenciaPanel = new RegistrarAsistenciaPanel(recepcionistaPanel);
        recepcionistaPanel.addView("Registrar asistencia", registrarAsistenciaPanel);
        recepcionistaPanel.showView("Registrar asistencia");



    }
}
