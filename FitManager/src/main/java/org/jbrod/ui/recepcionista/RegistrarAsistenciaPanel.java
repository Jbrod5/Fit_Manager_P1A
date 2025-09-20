package org.jbrod.ui.recepcionista;

import org.jbrod.controller.AsistenciaDB;
import org.jbrod.controller.ClientesDB;
import org.jbrod.controller.SucursalDB;
import org.jbrod.model.clientes_membresias.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrarAsistenciaPanel extends JPanel {

    private JComboBox<String> comboSucursales;
    private JTextField txtBusqueda;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrar;

    private ClientesDB clientesDB;
    private AsistenciaDB asistenciasDB;
    private RecepcionistaPanel recepcionistaPanel;



    private List<Cliente> listaClientes; // Para filtrado

    public RegistrarAsistenciaPanel(RecepcionistaPanel recepcionistaPanel) {
        this.recepcionistaPanel = recepcionistaPanel;
        clientesDB = new ClientesDB();
        asistenciasDB = AsistenciaDB.getInstance();
        setLayout(new BorderLayout(10,10));

        // === Arriba: combo sucursales + búsqueda ===
        // Panel contenedor de todo
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new GridLayout(2, 1, 5, 5)); // 2 filas, 1 columna, espacio entre filas

        // === Panel Sucursales ===
        JPanel panelSucursales = new JPanel(new GridLayout(1, 2, 5, 5)); // 1 fila, 2 columnas
        panelSucursales.add(new JLabel("Sucursal:"));
        comboSucursales = new JComboBox<>();
        cargarSucursales();
        panelSucursales.add(comboSucursales);

        // === Panel Búsqueda ===
        JPanel busquedaPanel = new JPanel(new GridLayout(1, 2, 5, 5)); // 1 fila, 2 columnas
        busquedaPanel.add(new JLabel("Buscar por nombre o correo:"));
        txtBusqueda = new JTextField();
        txtBusqueda.setToolTipText("Buscar por nombre o correo");
        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        busquedaPanel.add(txtBusqueda);

        // === Agregar subpaneles al panel superior ===
        panelSuperior.add(panelSucursales);
        panelSuperior.add(busquedaPanel);

        // === Agregar panel superior al panel principal ===
        add(panelSuperior, BorderLayout.NORTH);


        // === Tabla clientes ===
        String[] columnas = {"ID", "Nombre", "Correo"};
        modeloTabla = new DefaultTableModel(columnas,0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        cargarClientes();

        // === Botón registrar asistencia ===
        btnRegistrar = new JButton("Registrar asistencia");
        btnRegistrar.addActionListener(e -> registrarAsistencia());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnRegistrar);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void cargarSucursales() {
        comboSucursales = new JComboBox<>();
        SucursalDB sucursalDB = new SucursalDB();
        sucursalDB.obtenerSucursalesGimnasios().forEach(comboSucursales::addItem);
    }

    private void cargarClientes() {
        listaClientes = clientesDB.obtenerTodos();
        actualizarTabla(listaClientes);
    }

    private void actualizarTabla(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        for (Cliente c : clientes) {
            modeloTabla.addRow(new Object[]{c.getIdCliente(), c.getNombre(), c.getCorreo()});
        }
    }

    private void filtrar() {
        String texto = txtBusqueda.getText().trim().toLowerCase();
        List<Cliente> filtrados = listaClientes.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(texto) || c.getCorreo().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        actualizarTabla(filtrados);
    }

    private void registrarAsistencia() {
        int fila = tablaClientes.getSelectedRow();
        if(fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para registrar la asistencia.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extraer el correo del cliente de la tabla
        String correoCliente = (String) modeloTabla.getValueAt(fila, 2);
        int idSucursal = obtenerIdSucursal((String) comboSucursales.getSelectedItem());

        if(asistenciasDB.registrarAsistencia(correoCliente, idSucursal)) {
            JOptionPane.showMessageDialog(this, "Asistencia registrada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la asistencia.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private int obtenerIdSucursal(String nombreSucursal) {
        SucursalDB sucursalDB = new SucursalDB();
        return sucursalDB.obtenerIdPorNombre(nombreSucursal); // Este método lo tendrías que crear
    }

}
