package org.jbrod.controller;

import org.jbrod.model.clientes_membresias.Cliente;
import org.jbrod.util.DBConnectionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClientesDB {

    // === Obtener todos los clientes ===
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = """
        SELECT c.id_cliente, c.nombre, c.correo, c.fecha_registro,
               e.nombre AS nombre_entrenador
        FROM cliente c
        LEFT JOIN asignacion_entrenador a ON c.id_cliente = a.id_cliente
        LEFT JOIN empleado e ON a.id_entrenador = e.id_empleado
        ORDER BY c.id_cliente
    """;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getTimestamp("fecha_registro")
                );
                c.setNombreEntrenador(rs.getString("nombre_entrenador")); // asignamos el entrenador
                clientes.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientes;
    }


    // === Insertar cliente nuevo ===
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nombre, correo) VALUES (?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Actualizar cliente existente ===
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, correo = ? WHERE id_cliente = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setInt(3, cliente.getIdCliente());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Eliminar cliente por ID ===
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Obtener cliente por ID ===
    public Cliente obtenerPorId(int idCliente) {
        String sql = "SELECT id_cliente, nombre, correo, fecha_registro FROM cliente WHERE id_cliente = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getTimestamp("fecha_registro")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra
    }



}
