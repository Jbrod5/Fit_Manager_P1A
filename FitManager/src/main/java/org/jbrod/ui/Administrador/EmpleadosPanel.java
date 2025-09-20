package org.jbrod.ui.Administrador;

import org.jbrod.controller.EmpleadoDB;
import org.jbrod.model.empleados.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EmpleadosPanel extends JPanel {

    private JTextField txtBusqueda;
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoEmpleado;
    private JButton btnEditar;
    private JButton btnEliminar;

    private EmpleadoDB empleadoDB;
    private AdministradorPanel administradorPanel;

    private List<Empleado> listaEmpleados; // Para filtrado

    public EmpleadosPanel(AdministradorPanel administradorPanel) {
        this.administradorPanel = administradorPanel;
        empleadoDB = new EmpleadoDB();
        setLayout(new BorderLayout(10,10));

        // === Panel superior: Barra de búsqueda ===
        JPanel busquedaPanel = new JPanel(new BorderLayout(5,5));
        busquedaPanel.add(new JLabel("Buscar por nombre, usuario o correo:"), BorderLayout.WEST);
        txtBusqueda = new JTextField();
        txtBusqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        busquedaPanel.add(txtBusqueda, BorderLayout.CENTER);
        add(busquedaPanel, BorderLayout.NORTH);

        // === Tabla empleados ===
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol", "Sucursal"};
        modeloTabla = new DefaultTableModel(columnas,0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.setRowHeight(25);
        add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);

        // === Panel inferior: botones ===
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Nuevo empleado a la izquierda
        JPanel nuevoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoEmpleado = new JButton("Nuevo empleado");
        btnNuevoEmpleado.addActionListener(e -> abrirNuevoEmpleado());
        nuevoPanel.add(btnNuevoEmpleado);
        bottomPanel.add(nuevoPanel, BorderLayout.WEST);

        // Editar y Eliminar a la derecha
        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        btnEditar.addActionListener(e -> editarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());

        accionesPanel.add(btnEditar);
        accionesPanel.add(btnEliminar);
        bottomPanel.add(accionesPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Cargar empleados inicial
        cargarEmpleados();
    }

    public void cargarEmpleados() {
        listaEmpleados = empleadoDB.obtenerTodos();
        actualizarTabla(listaEmpleados);
    }

    private void actualizarTabla(List<Empleado> empleados) {
        modeloTabla.setRowCount(0);
        for (Empleado emp : empleados) {
            modeloTabla.addRow(new Object[]{
                    emp.getId_empleado(),
                    emp.getNombre(),
                    emp.getUsuario(),
                    emp.getNombreRol(),
                    emp.getNombreSucursal()
            });
        }
    }

    private void filtrar() {
        String texto = txtBusqueda.getText().trim().toLowerCase();
        List<Empleado> filtrados = listaEmpleados.stream()
                .filter(emp -> emp.getNombre().toLowerCase().contains(texto)
                        || emp.getUsuario().toLowerCase().contains(texto)
                        || emp.getCorreo().toLowerCase().contains(texto))
                .collect(Collectors.toList());
        actualizarTabla(filtrados);
    }

    private Empleado obtenerEmpleadoSeleccionado() {
        int fila = tablaEmpleados.getSelectedRow();
        if(fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        return empleadoDB.obtenerPorId(id);
    }

    private void abrirNuevoEmpleado() {
        NuevoEmpleadoPanel nuevoPanel = new NuevoEmpleadoPanel(administradorPanel, this);
        administradorPanel.getContentPanel().add(nuevoPanel, "NuevoEmpleado");
        administradorPanel.showView("NuevoEmpleado");
    }

    private void editarEmpleado() {
        Empleado emp = obtenerEmpleadoSeleccionado();
        if(emp != null) {
            EditarEmpleadoPanel editarPanel = new EditarEmpleadoPanel(administradorPanel, this, emp);
            administradorPanel.getContentPanel().add(editarPanel, "EditarEmpleado");
            administradorPanel.showView("EditarEmpleado");
        }
    }

    private void eliminarEmpleado() {
        Empleado emp = obtenerEmpleadoSeleccionado();
        if(emp != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas eliminar al empleado " + emp.getNombre() + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                empleadoDB.eliminar(emp.getId_empleado());
                cargarEmpleados();
            }
        }
    }
}
