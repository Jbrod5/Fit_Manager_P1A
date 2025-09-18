package org.jbrod.ui.Administrador;

import org.jbrod.controller.EmpleadoDB;
import org.jbrod.model.empleados.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmpleadosPanel extends JPanel {

    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoEmpleado;
    private EmpleadoDB empleadoDB;  // DAO para traer empleados

    private AdministradorPanel administradorPanel;

    public EmpleadosPanel(AdministradorPanel administradorPanel) {
        this.administradorPanel = administradorPanel;
        setLayout(new BorderLayout());

        empleadoDB = new EmpleadoDB();

        // === Botón nuevo empleado ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoEmpleado = new JButton("Nuevo empleado");
        btnNuevoEmpleado.addActionListener(e -> {
            Container parent = EmpleadosPanel.this.getParent(); // debería ser contentPanel
            while (parent != null && !(parent instanceof AdministradorPanel)) {
                parent = parent.getParent();
            }

            if (parent != null) {
                AdministradorPanel adminPanel = (AdministradorPanel) parent;

                // Crear el panel de nuevo empleado y agregarlo a contentPanel si no existe
                NuevoEmpleadoPanel nuevoPanel = new NuevoEmpleadoPanel(adminPanel, EmpleadosPanel.this);
                adminPanel.getContentPanel().add(nuevoPanel, "NuevoEmpleado");

                // Mostrar el panel de nuevo empleado
                CardLayout cl = (CardLayout) adminPanel.getContentPanel().getLayout();
                cl.show(adminPanel.getContentPanel(), "NuevoEmpleado");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo abrir el panel de nuevo empleado.");
            }
        });

        topPanel.add(btnNuevoEmpleado);
        add(topPanel, BorderLayout.NORTH);

        // === Tabla de empleados ===
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol", "Sucursal", "Editar", "Eliminar"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };

        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setRowHeight(30);

        // === Cargar empleados desde la BD ===
        cargarEmpleados();

        // === Renderizar botones en la tabla ===
        agregarBotones();

        add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);
    }

    public void cargarEmpleados() {
        try {
            if (tablaEmpleados.isEditing()) {
                tablaEmpleados.getCellEditor().stopCellEditing();
            }

            List<Empleado> empleados = empleadoDB.obtenerTodos();
            modeloTabla.setRowCount(0); // limpiar
            for (Empleado emp : empleados) {
                modeloTabla.addRow(new Object[]{
                        emp.getId_empleado(),
                        emp.getNombre(),
                        emp.getUsuario(),
                        emp.getNombreRol(),
                        emp.getNombreSucursal(),
                        "Editar",
                        "Eliminar"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar empleados: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void agregarBotones() {
        tablaEmpleados.getColumn("Editar").setCellRenderer(new BotonRenderer());
        tablaEmpleados.getColumn("Eliminar").setCellRenderer(new BotonRenderer());

        tablaEmpleados.getColumn("Editar").setCellEditor(new BotonEditor(new JCheckBox(), "Editar"));
        tablaEmpleados.getColumn("Eliminar").setCellEditor(new BotonEditor(new JCheckBox(), "Eliminar"));
    }

    // === Botón renderer ===
    class BotonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public BotonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // === Botón editor ===
    class BotonEditor extends DefaultCellEditor {
        private String label;
        private JButton button;
        private boolean clicked;

        public BotonEditor(JCheckBox checkBox, String tipo) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
            this.label = tipo;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int fila = tablaEmpleados.getSelectedRow();
                int id = (int) modeloTabla.getValueAt(fila, 0);

                if ("Editar".equals(label)) {
                    Empleado emp = empleadoDB.obtenerPorId(id);

                    if (emp != null) {
                        AdministradorPanel adminPanel = null;
                        Container parent = EmpleadosPanel.this.getParent();
                        while (parent != null && !(parent instanceof AdministradorPanel)) {
                            parent = parent.getParent();
                        }
                        if (parent != null) {
                            adminPanel = (AdministradorPanel) parent;
                        }

                        if (adminPanel != null) {
                            EditarEmpleadoPanel editarPanel = new EditarEmpleadoPanel(adminPanel, EmpleadosPanel.this, emp);
                            adminPanel.getContentPanel().add(editarPanel, "EditarEmpleado");

                            CardLayout cl = (CardLayout) adminPanel.getContentPanel().getLayout();
                            cl.show(adminPanel.getContentPanel(), "EditarEmpleado");
                        }

                        } else {
                            JOptionPane.showMessageDialog(null, "No se encontró el empleado.");
                        }

                    } else if ("Eliminar".equals(label)) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "¿Seguro que deseas eliminar al empleado con ID: " + id + "?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        empleadoDB.eliminar(id); // eliminar en BD
                        cargarEmpleados(); // refrescar tabla
                        administradorPanel.showInicioEmpleados();
                    }

                }
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

    }
}
