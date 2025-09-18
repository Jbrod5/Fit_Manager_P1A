package org.jbrod.controller;

import org.jbrod.util.DBConnectionSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDB {

    // Conexión única desde el singleton
    private static final Connection conn = DBConnectionSingleton.getInstance().getConnection();

    // === Obtener inventario con sucursales ===
    public static List<Object[]> obtenerInventarioConSucursales() {
        List<Object[]> data = new ArrayList<>();
        String sql = """
            SELECT s.nombre AS sucursal, e.nombre AS equipo, e.descripcion, i.cantidad
            FROM inventario i
            JOIN sucursal s ON i.id_sucursal = s.id_sucursal
            JOIN equipo e ON i.id_equipo = e.id_equipo
            ORDER BY s.nombre, e.nombre;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("sucursal"),
                        rs.getString("equipo"),
                        rs.getString("descripcion"),
                        rs.getInt("cantidad")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // === Obtener todos los equipos ===
    public static List<Object[]> obtenerEquipos() {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT id_equipo, nombre, descripcion FROM equipo ORDER BY nombre";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // === Agregar equipo ===
    public static boolean agregarEquipo(String nombre, String descripcion) {
        String sql = "INSERT INTO equipo (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Obtener nombres de equipos ===
    public static List<String> obtenerNombresEquipos() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM equipo ORDER BY nombre";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === Obtener nombres de sucursales ===
    public static List<String> obtenerNombresSucursales() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM sucursal ORDER BY nombre";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === Transferir equipo entre sucursales ===
    public static boolean transferirEquipo(String nombreEquipo, String desdeSucursal, String haciaSucursal, int cantidad) {
        String sqlInsert = "INSERT INTO transferencia_equipo (id_equipo, desde_id_sucursal, hacia_id_sucursal, cantidad) VALUES (?, ?, ?, ?)";
        String sqlUpdateOrigen = "UPDATE inventario SET cantidad = cantidad - ? WHERE id_equipo = ? AND id_sucursal = ?";
        String sqlUpdateDestino = """
            INSERT INTO inventario (id_equipo, id_sucursal, cantidad)
            VALUES (?, ?, ?)
            ON CONFLICT (id_equipo, id_sucursal) DO UPDATE SET cantidad = inventario.cantidad + EXCLUDED.cantidad
        """;

        try {
            conn.setAutoCommit(false);

            int idEquipo = obtenerIdEquipo(nombreEquipo, conn);
            int idDesde = obtenerIdSucursal(desdeSucursal, conn);
            int idHacia = obtenerIdSucursal(haciaSucursal, conn);

            // 1. Insertar en historial
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, idEquipo);
                ps.setInt(2, idDesde);
                ps.setInt(3, idHacia);
                ps.setInt(4, cantidad);
                ps.executeUpdate();
            }

            // 2. Restar inventario origen
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateOrigen)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idEquipo);
                ps.setInt(3, idDesde);
                ps.executeUpdate();
            }

            // 3. Sumar inventario destino
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateDestino)) {
                ps.setInt(1, idEquipo);
                ps.setInt(2, idHacia);
                ps.setInt(3, cantidad);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // === Métodos auxiliares ===
    private static int obtenerIdEquipo(String nombre, Connection conn) throws SQLException {
        String sql = "SELECT id_equipo FROM equipo WHERE nombre = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ps.setString(1, nombre);
            if (rs.next()) return rs.getInt("id_equipo");
        }
        throw new SQLException("Equipo no encontrado: " + nombre);
    }

    private static int obtenerIdSucursal(String nombre, Connection conn) throws SQLException {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ps.setString(1, nombre);
            if (rs.next()) return rs.getInt("id_sucursal");
        }
        throw new SQLException("Sucursal no encontrada: " + nombre);
    }

    // === Obtener transferencias ===
    public static List<Object[]> obtenerTransferencias() {
        List<Object[]> data = new ArrayList<>();
        String sql = """
            SELECT e.nombre AS equipo, s1.nombre AS desde, s2.nombre AS hacia,
                   t.cantidad, t.fecha
            FROM transferencia_equipo t
            JOIN equipo e ON t.id_equipo = e.id_equipo
            JOIN sucursal s1 ON t.desde_id_sucursal = s1.id_sucursal
            JOIN sucursal s2 ON t.hacia_id_sucursal = s2.id_sucursal
            ORDER BY t.fecha DESC;
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getString("equipo"),
                        rs.getString("desde"),
                        rs.getString("hacia"),
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fecha")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}
