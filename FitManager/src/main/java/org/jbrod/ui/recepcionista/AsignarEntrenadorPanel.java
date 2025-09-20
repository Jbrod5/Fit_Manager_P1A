package org.jbrod.ui.recepcionista;

import org.jbrod.controller.EntrenadorDB;
import org.jbrod.model.clientes_membresias.Cliente;
import org.jbrod.model.empleados.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AsignarEntrenadorPanel extends JPanel {

    private JTable tablaEntrenadores;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusquedaNombreCorreo;
    private JButton btnAsignar;

    private List<Empleado> listaEntrenadores;

    private RecepcionistaPanel recepcionistaPanel;
    private Cliente cliente; // Cliente al que se le asignará el entrenador


    public AsignarEntrenadorPanel(RecepcionistaPanel recepcionistaPanel ,Cliente cliente) {
        this.cliente = cliente;
        this.recepcionistaPanel = recepcionistaPanel;


        setLayout(new BorderLayout(10, 10));

        // === Panel superior: info y búsqueda ===
        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 5, 5));
        panelSuperior.add(new JLabel("Asignar entrenador a: " + cliente.getNombre()));
        panelSuperior.add(new JLabel()); // vacío
        panelSuperior.add(new JLabel("Buscar por nombre o correo:"));
        txtBusquedaNombreCorreo = new JTextField();
        panelSuperior.add(txtBusquedaNombreCorreo);

        txtBusquedaNombreCorreo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        add(panelSuperior, BorderLayout.NORTH);

        // === Tabla de entrenadores ===
        String[] columnas = {"ID", "Nombre", "Correo", "Teléfono", "Sucursal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaEntrenadores = new JTable(modeloTabla);
        tablaEntrenadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaEntrenadores), BorderLayout.CENTER);

        // === Botón asignar ===
        btnAsignar = new JButton("Asignar entrenador");
        btnAsignar.addActionListener(e -> asignarEntrenadorSeleccionado());
        add(btnAsignar, BorderLayout.SOUTH);

        cargarEntrenadores();
    }

    private void cargarEntrenadores() {
        listaEntrenadores = EntrenadorDB.obtenerEntrenadores();
        actualizarTabla(listaEntrenadores);
    }

    // En actualizarTabla
    private void actualizarTabla(List<Empleado> entrenadores) {
        modeloTabla.setRowCount(0);
        for (Empleado e : entrenadores) {
            modeloTabla.addRow(new Object[]{
                    e.getId_empleado(),
                    e.getNombre(),
                    e.getCorreo(),
                    e.getTelefono(),
                    e.getNombreSucursal()
            });
        }
    }


    private void filtrar() {
        String texto = txtBusquedaNombreCorreo.getText().trim().toLowerCase();
        List<Empleado> filtrados = listaEntrenadores.stream()
                .filter(e -> e.getNombre().toLowerCase().contains(texto)
                        || e.getCorreo().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        actualizarTabla(filtrados);
    }

    private void asignarEntrenadorSeleccionado() {
        int fila = tablaEntrenadores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEntrenador = (int) modeloTabla.getValueAt(fila, 0);

        boolean exito = EntrenadorDB.asignarEntrenador(cliente.getIdCliente(), idEntrenador);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Entrenador asignado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            recepcionistaPanel.showInicioClientes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo asignar el entrenador.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
