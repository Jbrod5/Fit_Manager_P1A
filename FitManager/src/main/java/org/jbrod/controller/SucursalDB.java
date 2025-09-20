package org.jbrod.controller;

import org.jbrod.util.DBConnectionSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SucursalDB {

    // Obtiene todas las sucursales que son gimnasios
    public List<String> obtenerSucursalesGimnasios() {
        List<String> sucursales = new ArrayList<>();
        String sql = "SELECT nombre FROM sucursal WHERE id_tipo = 2"; // 2 = Gimnasio

        try (Connection con = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                sucursales.add(rs.getString("nombre"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sucursales;
    }

    public int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre = ?";
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt("id_sucursal");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // No encontrado
    }

}
