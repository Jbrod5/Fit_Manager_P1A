package org.jbrod.controller;

import org.jbrod.model.clientes_membresias.MembresiaCliente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jbrod.util.DBConnectionSingleton;

public class MembresiasDB {

    // === Obtener la última membresía de todos los clientes ===
    public static List<MembresiaCliente> obtenerUltimasMembresiasTodosClientes() {
        List<MembresiaCliente> lista = new ArrayList<>();
        String sql = """
            SELECT c.id_cliente, c.nombre, c.correo,
                   mc.id_membresia_cliente, tmc.id_tipo_membresia, tmc.nombre_membresia,
                   mc.fecha_inicio, mc.fecha_fin
            FROM cliente c
            LEFT JOIN membresia_cliente mc ON c.id_cliente = mc.id_cliente
            LEFT JOIN tipo_membresia_cliente tmc ON mc.id_tipo_membresia = tmc.id_tipo_membresia
            WHERE mc.id_membresia_cliente IS NULL
               OR mc.id_membresia_cliente = (
                   SELECT MAX(id_membresia_cliente)
                   FROM membresia_cliente
                   WHERE id_cliente = c.id_cliente
               )
            ORDER BY c.id_cliente;
        """;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                int idMembresia = rs.getInt("id_membresia_cliente");
                int idTipo = rs.getInt("id_tipo_membresia");
                String nombreTipo = rs.getString("nombre_membresia");
                Date fechaIni = rs.getDate("fecha_inicio");
                Date fechaFin = rs.getDate("fecha_fin");

                lista.add(new MembresiaCliente(
                        idMembresia,
                        idCliente,
                        nombre,
                        correo,
                        idTipo,
                        nombreTipo,
                        (fechaIni != null) ? fechaIni.toLocalDate() : null,
                        (fechaFin != null) ? fechaFin.toLocalDate() : null
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === Registrar una membresía y crear pago automáticamente ===
    public static boolean registrarMembresia(int idCliente, int idTipoMembresia, LocalDate fechaInicio, LocalDate fechaFin, double monto) {
        String sqlMembresia = "INSERT INTO membresia_cliente (id_cliente, id_tipo_membresia, fecha_inicio, fecha_fin) VALUES (?,?,?,?) RETURNING id_membresia_cliente";
        String sqlPago = "INSERT INTO pago (id_cliente, id_membresia, monto, tipo_servicio, descripcion) VALUES (?,?,?,?,?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmtM = connection.prepareStatement(sqlMembresia);
             PreparedStatement stmtP = connection.prepareStatement(sqlPago)) {

            connection.setAutoCommit(false); // transacción

            // Membresía
            stmtM.setInt(1, idCliente);
            stmtM.setInt(2, idTipoMembresia);
            stmtM.setDate(3, Date.valueOf(fechaInicio));
            stmtM.setDate(4, Date.valueOf(fechaFin));

            ResultSet rs = stmtM.executeQuery();
            int idMembresia = -1;
            if (rs.next()) {
                idMembresia = rs.getInt(1);
            } else {
                connection.rollback();
                return false;
            }

            // Pago
            //stmtP.setInt(1, idCliente);
            //stmtP.setInt(2, idMembresia);
            //stmtP.setDouble(3, monto);
            //stmtP.setString(4, "Membresía");
            //stmtP.setString(5, "Pago automático al registrar membresía");
            //stmtP.executeUpdate();

            //Registrar el pago
            PagosDB.registrarPago(idCliente, idMembresia,100, "Membresia", "Pago automatico al registarr membresia");

            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }




    }

    // === Obtener historial completo de membresías de un cliente ===
    public static List<MembresiaCliente> obtenerHistorialMembresias(int idCliente) {
        List<MembresiaCliente> lista = new ArrayList<>();
        String sql = """
            SELECT mc.id_membresia_cliente, c.id_cliente, c.nombre, c.correo,
                   tmc.id_tipo_membresia, tmc.nombre_membresia, mc.fecha_inicio, mc.fecha_fin
            FROM membresia_cliente mc
            JOIN cliente c ON mc.id_cliente = c.id_cliente
            JOIN tipo_membresia_cliente tmc ON mc.id_tipo_membresia = tmc.id_tipo_membresia
            WHERE c.id_cliente = ?
            ORDER BY mc.fecha_inicio DESC
        """;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new MembresiaCliente(
                        rs.getInt("id_membresia_cliente"),
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("id_tipo_membresia"),
                        rs.getString("nombre_membresia"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Obtener todos los nombres de tipos de membresía
    public static List<String> obtenerTiposMembresia() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre_membresia FROM tipo_membresia_cliente ORDER BY id_tipo_membresia";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                lista.add(rs.getString("nombre_membresia"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener id por nombre
    public static int obtenerIdTipoMembresiaPorNombre(String nombre) {
        String sql = "SELECT id_tipo_membresia FROM tipo_membresia_cliente WHERE nombre_membresia = ?";
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt("id_tipo_membresia");
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    // === Obtener historial completo de todas las membresías (todos los clientes) ===
    public static List<MembresiaCliente> obtenerHistorialMembresiasTodos() {
        List<MembresiaCliente> lista = new ArrayList<>();
        String sql = """
        SELECT mc.id_membresia_cliente, c.id_cliente, c.nombre, c.correo,
               tmc.id_tipo_membresia, tmc.nombre_membresia, mc.fecha_inicio, mc.fecha_fin
        FROM membresia_cliente mc
        JOIN cliente c ON mc.id_cliente = c.id_cliente
        JOIN tipo_membresia_cliente tmc ON mc.id_tipo_membresia = tmc.id_tipo_membresia
        ORDER BY mc.fecha_inicio DESC
    """;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new MembresiaCliente(
                        rs.getInt("id_membresia_cliente"),
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("id_tipo_membresia"),
                        rs.getString("nombre_membresia"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }



}
