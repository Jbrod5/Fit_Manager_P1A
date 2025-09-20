package org.jbrod.controller;

import org.jbrod.model.pagos_asistencias.Pago;
import org.jbrod.util.DBConnectionSingleton;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagosDB {

    // === Obtener todos los pagos ===
    public static List<Pago> obtenerPagos() {
        List<Pago> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_pago, p.id_cliente, c.nombre, c.correo,
                   COALESCE(p.id_membresia, -1) AS id_membresia,
                   p.monto, p.fecha_pago, p.tipo_servicio, p.descripcion
            FROM pago p
            JOIN cliente c ON p.id_cliente = c.id_cliente
            ORDER BY p.fecha_pago DESC, p.id_pago DESC
        """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Pago(
                        rs.getInt("id_pago"),
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("id_membresia"),
                        rs.getDouble("monto"),
                        rs.getDate("fecha_pago").toLocalDate(),
                        rs.getString("tipo_servicio"),
                        rs.getString("descripcion")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === Buscar pagos por nombre o correo ===
    public static List<Pago> buscarPagos(String filtro) {
        List<Pago> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_pago, p.id_cliente, c.nombre, c.correo,
                   COALESCE(p.id_membresia, -1) AS id_membresia,
                   p.monto, p.fecha_pago, p.tipo_servicio, p.descripcion
            FROM pago p
            JOIN cliente c ON p.id_cliente = c.id_cliente
            WHERE LOWER(c.nombre) LIKE LOWER(?) OR LOWER(c.correo) LIKE LOWER(?)
            ORDER BY p.fecha_pago DESC, p.id_pago DESC
        """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String filtroLike = "%" + filtro + "%";
            ps.setString(1, filtroLike);
            ps.setString(2, filtroLike);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Pago(
                        rs.getInt("id_pago"),
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("id_membresia"),
                        rs.getDouble("monto"),
                        rs.getDate("fecha_pago").toLocalDate(),
                        rs.getString("tipo_servicio"),
                        rs.getString("descripcion")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === Registrar un nuevo pago manual ===
    public static boolean registrarPago(int idCliente, Integer idMembresia,
                                        double monto, String tipoServicio, String descripcion) {
        String sql = """
            INSERT INTO pago (id_cliente, id_membresia, monto, tipo_servicio, descripcion)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            if (idMembresia != null) {
                ps.setInt(2, idMembresia);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setDouble(3, monto);
            ps.setString(4, tipoServicio);
            ps.setString(5, descripcion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
