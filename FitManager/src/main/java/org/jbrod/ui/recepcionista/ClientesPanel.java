package org.jbrod.ui.recepcionista;

import org.jbrod.controller.ClientesDB;

import org.jbrod.model.clientes_membresias.Cliente;
import org.jbrod.ui.recepcionista.RecepcionistaPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientesPanel extends JPanel {
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoCliente;
    private ClientesDB clientesDB;

    private RecepcionistaPanel recepcionistaPanel;

    public ClientesPanel(RecepcionistaPanel recepcionistaPanel) {
        this.recepcionistaPanel = recepcionistaPanel;
        setLayout(new BorderLayout());

        clientesDB = new ClientesDB();

        // === Botón nuevo cliente ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoCliente = new JButton("Nuevo cliente");
        btnNuevoCliente.addActionListener(e -> {
            Container parent = ClientesPanel.this.getParent();
            while (parent != null && !(parent instanceof RecepcionistaPanel)) {
                parent = parent.getParent();
            }

            if (parent != null) {
                //RecepcionistaPanel recPanel = (RecepcionistaPanel) parent;
                //NuevoClientePanel nuevoPanel = new NuevoClientePanel(recPanel, ClientesPanel.this);
                //recPanel.getContentPanel().add(nuevoPanel, "NuevoCliente");

                //CardLayout cl = (CardLayout) recPanel.getContentPanel().getLayout();
                //cl.show(recPanel.getContentPanel(), "NuevoCliente");


                AgregarClientePanel agregarClientePanel = new AgregarClientePanel(recepcionistaPanel, this);
                recepcionistaPanel.addView("Agregar cliente", agregarClientePanel);
                recepcionistaPanel.showView("Agregar cliente");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo abrir el panel de nuevo cliente.");
            }
        });

        topPanel.add(btnNuevoCliente);
        add(topPanel, BorderLayout.NORTH);

        // === Tabla de clientes ===
        String[] columnas = {"ID", "Nombre", "Correo", "Fecha Registro", "Editar", "Eliminar"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(30);

        // === Cargar clientes desde la BD ===
        cargarClientes();

        // === Renderizar botones ===
        agregarBotones();

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
    }

    public void cargarClientes() {
        try {
            if (tablaClientes.isEditing()) {
                tablaClientes.getCellEditor().stopCellEditing();
            }

            List<Cliente> clientes = clientesDB.obtenerTodos();
            modeloTabla.setRowCount(0);

            for (Cliente c : clientes) {
                modeloTabla.addRow(new Object[]{
                        c.getIdCliente(),
                        c.getNombre(),
                        c.getCorreo(),
                        c.getFechaRegistro(),
                        "Editar",
                        "Eliminar"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar clientes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarBotones() {
        tablaClientes.getColumn("Editar").setCellRenderer(new BotonRenderer());
        tablaClientes.getColumn("Eliminar").setCellRenderer(new BotonRenderer());

        tablaClientes.getColumn("Editar").setCellEditor(new BotonEditor(new JCheckBox(), "Editar"));
        tablaClientes.getColumn("Eliminar").setCellEditor(new BotonEditor(new JCheckBox(), "Eliminar"));
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
                int fila = tablaClientes.getSelectedRow();
                int id = (int) modeloTabla.getValueAt(fila, 0);

                if ("Editar".equals(label)) {
                    Cliente cliente = clientesDB.obtenerPorId(id);

                    if (cliente != null) {
                        RecepcionistaPanel recPanel = null;
                        Container parent = ClientesPanel.this.getParent();
                        while (parent != null && !(parent instanceof RecepcionistaPanel)) {
                            parent = parent.getParent();
                        }
                        if (parent != null) {
                            recPanel = (RecepcionistaPanel) parent;
                        }

                        if (recPanel != null) {
                            EditarClientePanel editarPanel = new EditarClientePanel(recPanel, ClientesPanel.this, cliente);
                            recPanel.getContentPanel().add(editarPanel, "EditarCliente");

                            CardLayout cl = (CardLayout) recPanel.getContentPanel().getLayout();
                            cl.show(recPanel.getContentPanel(), "EditarCliente");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró el cliente.");
                    }

                } else if ("Eliminar".equals(label)) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "¿Seguro que deseas eliminar al cliente con ID: " + id + "?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        clientesDB.eliminar(id);
                        cargarClientes();
                        recepcionistaPanel.showInicioClientes();
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
