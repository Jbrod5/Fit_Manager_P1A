package org.jbrod.ui.recepcionista;

import org.jbrod.controller.ClientesDB;
import org.jbrod.model.clientes_membresias.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ClientesPanel extends JPanel {

    private JTextField txtBusqueda;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoCliente;
    private JButton btnEditar;
    private JButton btnEliminar;

    private ClientesDB clientesDB;
    private RecepcionistaPanel recepcionistaPanel;

    private List<Cliente> listaClientes; // Para filtrado

    public ClientesPanel(RecepcionistaPanel recepcionistaPanel) {
        this.recepcionistaPanel = recepcionistaPanel;
        clientesDB = new ClientesDB();
        setLayout(new BorderLayout(10,10));

        // === Panel superior: Barra de búsqueda ===
        JPanel busquedaPanel = new JPanel(new BorderLayout(5,5));
        busquedaPanel.add(new JLabel("Buscar por nombre o correo:"), BorderLayout.WEST);
        txtBusqueda = new JTextField();
        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        busquedaPanel.add(txtBusqueda, BorderLayout.CENTER);

        add(busquedaPanel, BorderLayout.NORTH);

        // === Tabla clientes ===
        String[] columnas = {"ID", "Nombre", "Correo", "Fecha Registro"};
        modeloTabla = new DefaultTableModel(columnas,0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(25);

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        // === Panel inferior: botones ===
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Nuevo cliente a la izquierda
        JPanel nuevoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoCliente = new JButton("Nuevo cliente");
        btnNuevoCliente.addActionListener(e -> abrirAgregarCliente());
        nuevoPanel.add(btnNuevoCliente);
        bottomPanel.add(nuevoPanel, BorderLayout.WEST);

        // Editar y Eliminar a la derecha
        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnEditar.addActionListener(e -> editarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());

        accionesPanel.add(btnEditar);
        accionesPanel.add(btnEliminar);
        bottomPanel.add(accionesPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Cargar clientes inicial
        cargarClientes();
    }

    private void abrirAgregarCliente() {
        AgregarClientePanel agregarClientePanel = new AgregarClientePanel(recepcionistaPanel, this);
        recepcionistaPanel.addView("Agregar cliente", agregarClientePanel);
        recepcionistaPanel.showView("Agregar cliente");
    }

    public void cargarClientes() {
        listaClientes = clientesDB.obtenerTodos();
        actualizarTabla(listaClientes);
    }

    private void actualizarTabla(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        for (Cliente c : clientes) {
            modeloTabla.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getCorreo(),
                    c.getFechaRegistro()
            });
        }
    }

    private void filtrar() {
        String texto = txtBusqueda.getText().trim().toLowerCase();
        List<Cliente> filtrados = listaClientes.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(texto) || c.getCorreo().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        actualizarTabla(filtrados);
    }

    private Cliente obtenerClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if(fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return clientesDB.obtenerPorId(id);
    }

    private void editarCliente() {
        Cliente cliente = obtenerClienteSeleccionado();
        if(cliente != null) {
            EditarClientePanel editarPanel = new EditarClientePanel(recepcionistaPanel, this, cliente);
            recepcionistaPanel.addView("Editar cliente", editarPanel);
            recepcionistaPanel.showView("Editar cliente");
        }
    }

    private void eliminarCliente() {
        Cliente cliente = obtenerClienteSeleccionado();
        if(cliente != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas eliminar al cliente " + cliente.getNombre() + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                clientesDB.eliminar(cliente.getIdCliente());
                cargarClientes();
            }
        }
    }
}
