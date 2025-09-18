package org.jbrod.ui.Administrador;

import org.jbrod.controller.InventarioDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventarioPanel extends JPanel {

    private JPanel contentPanel;
    private JTable equiposTable;
    private JTable inventarioTable;
    private JTable transferenciasTable;
    private DefaultTableModel equiposModel;
    private DefaultTableModel inventarioModel;
    private DefaultTableModel transferenciasModel;

    private AdministradorPanel administradorPanel;

    public InventarioPanel(AdministradorPanel administradorPanel) {
        setLayout(new BorderLayout());
        this.administradorPanel = administradorPanel;

        // 🔹 Botones de acciones
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVerInventario = new JButton("Ver Inventario");
        JButton btnRegistrar = new JButton("Registrar Equipo");
        JButton btnTransferir = new JButton("Transferir Equipos");

        actionsPanel.add(btnVerInventario);
        actionsPanel.add(btnRegistrar);
        actionsPanel.add(btnTransferir);

        // 🔹 Content dinámico
        contentPanel = new JPanel(new CardLayout());

        // 👉 Panel de equipos registrados
        String[] columnasEquipos = {"ID", "Nombre", "Descripción"};
        equiposModel = new DefaultTableModel(columnasEquipos, 0);
        equiposTable = new JTable(equiposModel);
        JScrollPane scrollEquipos = new JScrollPane(equiposTable);

        // 👉 Panel de inventario con JTable
        String[] columnasInventario = {"Sucursal", "Equipo", "Descripción", "Cantidad"};
        inventarioModel = new DefaultTableModel(columnasInventario, 0);
        inventarioTable = new JTable(inventarioModel);
        JScrollPane scrollInventario = new JScrollPane(inventarioTable);

        // 👉 Panel de transferencias realizadas
        String[] columnasTransferencias = {"Equipo", "Desde", "Hacia", "Cantidad", "Fecha"};
        transferenciasModel = new DefaultTableModel(columnasTransferencias, 0);
        transferenciasTable = new JTable(transferenciasModel);
        JScrollPane scrollTransferencias = new JScrollPane(transferenciasTable);

        // 👉 Panel combinado (tres secciones una debajo de otra)
        JPanel inventarioPanel = new JPanel();
        inventarioPanel.setLayout(new BoxLayout(inventarioPanel, BoxLayout.Y_AXIS));
        inventarioPanel.add(new JLabel("Equipos registrados:"));
        inventarioPanel.add(scrollEquipos);
        inventarioPanel.add(Box.createVerticalStrut(10));
        inventarioPanel.add(new JLabel("Inventario por sucursal:"));
        inventarioPanel.add(scrollInventario);
        inventarioPanel.add(Box.createVerticalStrut(10));
        inventarioPanel.add(new JLabel("Transferencias realizadas:"));
        inventarioPanel.add(scrollTransferencias);

        // 🔹 Agregar paneles al CardLayout
        contentPanel.add(inventarioPanel, "Ver");
        contentPanel.add(new JLabel("Formulario registrar", SwingConstants.CENTER), "Registrar");
        contentPanel.add(new JLabel("Formulario transferir", SwingConstants.CENTER), "Transferir");

        // Acciones de los botones
        btnVerInventario.addActionListener(e -> {
            cargarDatos();
            showView("Ver");
        });

        btnRegistrar.addActionListener(e -> mostrarRegistro());
        btnTransferir.addActionListener(e -> mostrarTransferencia());

        add(actionsPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        cargarDatos();
        showView("Ver");
    }

    private void showView(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    // === Método seguro para cargar o recargar todas las tablas ===
    public void cargarDatos() {
        // Limpiar tablas
        equiposModel.setRowCount(0);
        inventarioModel.setRowCount(0);
        transferenciasModel.setRowCount(0);

        // Cargar equipos
        List<Object[]> equipos = InventarioDB.obtenerEquipos();
        if (equipos != null) {
            for (Object[] fila : equipos) {
                if (fila != null) equiposModel.addRow(fila);
            }
        }

        // Cargar inventario
        List<Object[]> inventario = InventarioDB.obtenerInventarioConSucursales();
        if (inventario != null) {
            for (Object[] fila : inventario) {
                if (fila != null) inventarioModel.addRow(fila);
            }
        }

        // Cargar transferencias
        List<Object[]> transferencias = InventarioDB.obtenerTransferencias();
        if (transferencias != null) {
            for (Object[] fila : transferencias) {
                if (fila != null) transferenciasModel.addRow(fila);
            }
        }
    }

    private void mostrarRegistro() {
        cargarDatos();
        administradorPanel.addView("Registrar equipo", new AgregarEquipoPanel(administradorPanel, this));
    }

    private void mostrarTransferencia() {
        cargarDatos();
        administradorPanel.addView("Registrar transferencia", new TransferenciaEquipoPanel(administradorPanel, this));
    }
}
