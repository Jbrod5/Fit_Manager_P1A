package org.jbrod.controller;

import org.jbrod.model.clientes_membresias.EntrenadorAsignacionHistorial;
import org.jbrod.model.empleados.Empleado;
import org.jbrod.util.DBConnectionSingleton;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDB {

    // Obtener todos los entrenadores junto con el nombre de su sucursal
    public static List<Empleado> obtenerEntrenadores() {
        List<Empleado> lista = new ArrayList<>();
        String sql = """
            SELECT e.id_empleado, e.nombre, e.correo, e.telefono, e.rol,
                   s.nombre AS nombre_sucursal
            FROM empleado e
            JOIN sucursal s ON e.id_sucursal = s.id_sucursal
            WHERE e.rol = (SELECT id_rol_empleado FROM rol_empleado WHERE nombre_rol = 'Entrenador')
            ORDER BY e.nombre
        """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Empleado e = new Empleado(
                        rs.getInt("id_empleado"),
                        -1, // id_sucursal no necesitamos, usamos nombre_sucursal
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("telefono"),
                        rs.getInt("rol"),
                        null, null
                );
                e.setNombreSucursal(rs.getString("nombre_sucursal")); // Necesitarás agregar este campo en Empleado
                lista.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // Asignar entrenador a un cliente y registrar en historial
    public static boolean asignarEntrenador(int idCliente, int idEntrenador) {
        String sqlAsignacion = """
            INSERT INTO asignacion_entrenador (id_cliente, id_entrenador)
            VALUES (?, ?)
            ON CONFLICT (id_cliente) 
            DO UPDATE SET id_entrenador = EXCLUDED.id_entrenador
        """;

        String sqlHistorial = """
            INSERT INTO historial_asignaciones_entrenador (id_cliente, id_entrenador, fecha_asignacion)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Transacción

            try (PreparedStatement ps1 = conn.prepareStatement(sqlAsignacion);
                 PreparedStatement ps2 = conn.prepareStatement(sqlHistorial)) {

                // Asignación actual
                ps1.setInt(1, idCliente);
                ps1.setInt(2, idEntrenador);
                ps1.executeUpdate();

                // Registro en historial
                ps2.setInt(1, idCliente);
                ps2.setInt(2, idEntrenador);
                ps2.setDate(3, Date.valueOf(LocalDate.now()));
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    // Obtener historial completo o filtrado por cliente
    public static List<EntrenadorAsignacionHistorial> obtenerHistorial(String filtroCliente) {
        List<EntrenadorAsignacionHistorial> lista = new ArrayList<>();
        String sql = """
            SELECT h.id_cliente, c.nombre AS nombre_cliente, c.correo AS correo_cliente,
                           h.id_entrenador, e.nombre AS nombre_entrenador, e.correo AS correo_entrenador,
                           h.fecha_asignacion, s.nombre AS nombre_sucursal
                    FROM historial_asignaciones_entrenador h
                    JOIN cliente c ON h.id_cliente = c.id_cliente
                    JOIN empleado e ON h.id_entrenador = e.id_empleado
                    JOIN sucursal s ON e.id_sucursal = s.id_sucursal
                    WHERE c.nombre ILIKE ? OR c.correo ILIKE ?
                    ORDER BY h.fecha_asignacion DESC
                
        """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String filtro = "%" + (filtroCliente != null ? filtroCliente.toLowerCase() : "") + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new EntrenadorAsignacionHistorial(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_cliente"),
                        rs.getString("correo_cliente"),
                        rs.getInt("id_entrenador"),
                        rs.getString("nombre_entrenador"),
                        rs.getString("correo_entrenador"),
                        rs.getDate("fecha_asignacion"),
                        rs.getString("nombre_sucursal")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
