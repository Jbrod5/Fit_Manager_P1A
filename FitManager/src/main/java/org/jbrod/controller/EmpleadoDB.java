package org.jbrod.controller;


import org.jbrod.model.empleados.Empleado;
import org.jbrod.util.DBConnectionSingleton;
import org.jbrod.util.PassEncrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDB {

    private Connection connection;

    public EmpleadoDB() {
        this.connection = DBConnectionSingleton.getInstance().getConnection();
    }

    // === Obtener todos los empleados ===
    public List<Empleado> obtenerTodos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT e.id_empleado, e.id_sucursal, e.nombre, e.correo, e.telefono, "
                + "e.rol AS rol_id, e.usuario, e.passwrd, "
                + "r.nombre_rol AS nombre_rol, s.nombre AS nombre_sucursal "
                + "FROM empleado e "
                + "JOIN rol_empleado r ON e.rol = r.id_rol_empleado "
                + "JOIN sucursal s ON e.id_sucursal = s.id_sucursal";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Empleado emp = new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getInt("id_sucursal"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("telefono"),
                        rs.getInt("rol_id"),       // tu ID del rol
                        rs.getString("usuario"),
                        rs.getString("passwrd")
                );

                // Guardamos los nombres de rol y sucursal
                emp.setNombreRol(rs.getString("nombre_rol"));
                emp.setNombreSucursal(rs.getString("nombre_sucursal"));

                empleados.add(emp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleados;
    }


    // === Insertar empleado nuevo ===
    public boolean insertar(Empleado emp) {
        String pass = PassEncrypt.encrytp(emp.getUsuario(), emp.getPassword());
        String sql = "INSERT INTO empleado (id_sucursal, nombre, correo, telefono, rol, usuario, passwrd) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, emp.getId_sucursal());
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getCorreo());
            ps.setInt(4, emp.getTelefono());
            ps.setInt(5, emp.getRolEmpleadoInt());
            ps.setString(6, emp.getUsuario());
            ps.setString(7, pass);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Actualizar empleado existente ===
    public boolean actualizar(Empleado emp) {
        String pass = PassEncrypt.encrytp(emp.getUsuario(), emp.getPassword());
        //Pero actualizar la contraseña solo si se cambio la contraseña :c

        String sql = "UPDATE empleado SET id_sucursal = ?, nombre = ?, correo = ?, telefono = ?, rol = ?, usuario = ?, passwrd = ? WHERE id_empleado = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, emp.getId_sucursal());
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getCorreo());
            ps.setInt(4, emp.getTelefono());
            ps.setInt(5, emp.getRolEmpleadoInt());
            ps.setString(6, emp.getUsuario());
            ps.setString(7, emp.getPassword());
            ps.setInt(8, emp.getId_empleado());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Eliminar empleado por ID ===
    public boolean eliminar(int idEmpleado) {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
