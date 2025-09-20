package org.jbrod.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.jbrod.util.DBConnectionSingleton;


public class AsistenciaDB {

    private static AsistenciaDB instance;

    private AsistenciaDB() {}

    public static AsistenciaDB getInstance() {
        if (instance == null) {
            instance = new AsistenciaDB();
        }
        return instance;
    }

    // Registrar asistencia
    public boolean registrarAsistencia(String correoCliente, int idSucursal) {

        System.out.println("Se intentara registrar la asistencia de: " + correoCliente);
        System.out.println();
        String sqlCliente = "SELECT id_cliente FROM cliente WHERE correo = ?";
        String sqlTipo = "SELECT ts.tipo FROM sucursal s JOIN tipo_sucursal ts ON s.id_tipo = ts.id_tipo WHERE s.id_sucursal = ?";
        String sqlInsert = "INSERT INTO asistencia (id_cliente, id_sucursal) VALUES (?, ?)";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement psCliente = conn.prepareStatement(sqlCliente);
             PreparedStatement psTipo = conn.prepareStatement(sqlTipo);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

            // Verificar cliente
            psCliente.setString(1, correoCliente);
            ResultSet rsCliente = psCliente.executeQuery();
            if (!rsCliente.next()) {
                System.out.println("Cliente no encontrado");
                return false;
            }
            int idCliente = rsCliente.getInt("id_cliente");

            // Verificar tipo de sucursal
            psTipo.setInt(1, idSucursal);
            ResultSet rsTipo = psTipo.executeQuery();
            if (rsTipo.next()) {
                String tipo = rsTipo.getString("tipo");
                if (!"Gimnasio".equalsIgnoreCase(tipo)) {
                    System.out.println("No se puede registrar asistencia en una bodega");
                    return false;
                }
            }

            // Insertar asistencia
            psInsert.setInt(1, idCliente);
            psInsert.setInt(2, idSucursal);
            return psInsert.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todas las asistencias
    public List<Object[]> obtenerAsistencias() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT a.id_asistencia, c.nombre, c.correo, s.nombre AS sucursal, a.entrada " +
                "FROM asistencia a " +
                "JOIN cliente c ON a.id_cliente = c.id_cliente " +
                "JOIN sucursal s ON a.id_sucursal = s.id_sucursal " +
                "ORDER BY a.entrada DESC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_asistencia"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("sucursal"),
                        rs.getTimestamp("entrada")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener asistencias por correo
    public List<Object[]> obtenerAsistenciasPorCorreo(String correo) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT a.id_asistencia, c.nombre, c.correo, s.nombre AS sucursal, a.entrada " +
                "FROM asistencia a " +
                "JOIN cliente c ON a.id_cliente = c.id_cliente " +
                "JOIN sucursal s ON a.id_sucursal = s.id_sucursal " +
                "WHERE c.correo = ? ORDER BY a.entrada DESC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_asistencia"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("sucursal"),
                        rs.getTimestamp("entrada")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


}
