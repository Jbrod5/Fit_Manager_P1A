package org.jbrod.controller;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.util.DBConnectionSingleton;
import org.jbrod.util.PassEncrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Autenticar {

    public Autenticar() {

    }

    public Empleado login(String usuario, String pass) throws SQLException {
        // Siempre usamos la conexión del singleton
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        // Encriptamos la contraseña ingresada
        String hashIngresado = PassEncrypt.encrytp(usuario, pass);

        String sql = "SELECT id_empleado, id_sucursal, nombre, correo, telefono, rol, usuario, passwrd " +
                "FROM empleado WHERE usuario=? AND passwrd=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, hashIngresado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si coincide, creamos un objeto Empleado
                    return new Empleado(
                            rs.getInt("id_empleado"),
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getInt("telefono"),
                            rs.getInt("rol"),
                            rs.getString("usuario"),
                            rs.getString("passwrd")
                    );
                } else {
                    return null; // Usuario o contraseña incorrectos
                }
            }
        }
    }
}
