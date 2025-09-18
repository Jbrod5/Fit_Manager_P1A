package org.jbrod.controller;

import org.jbrod.model.empleados.Empleado;
import org.jbrod.model.empleados.RolEmpleado;
import org.jbrod.util.DBConnectionSingleton;
import org.jbrod.util.PassEncrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDB {

    // === Obtener todos los empleados ===
    public List<Empleado> obtenerTodos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = """
            SELECT e.id_empleado, e.id_sucursal, e.nombre, e.correo, e.telefono,
                   e.rol AS rol_id, e.usuario, e.passwrd,
                   r.nombre_rol AS nombre_rol, s.nombre AS nombre_sucursal
            FROM empleado e
            JOIN rol_empleado r ON e.rol = r.id_rol_empleado
            JOIN sucursal s ON e.id_sucursal = s.id_sucursal
        """;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Empleado emp = new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getInt("id_sucursal"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("telefono"),
                        rs.getInt("rol_id"),
                        rs.getString("usuario"),
                        rs.getString("passwrd")
                );

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

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, emp.getId_sucursal());
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getCorreo());
            ps.setInt(4, emp.getTelefono());
            ps.setInt(5, emp.getRolEmpleadoInt());
            ps.setString(6, emp.getUsuario());
            ps.setString(7, pass);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Actualizar empleado existente ===
    public boolean actualizar(Empleado emp) {
        String pass = PassEncrypt.encrytp(emp.getUsuario(), emp.getPassword());
        String sql = "UPDATE empleado SET id_sucursal = ?, nombre = ?, correo = ?, telefono = ?, rol = ?, usuario = ?, passwrd = ? WHERE id_empleado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, emp.getId_sucursal());
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getCorreo());
            ps.setInt(4, emp.getTelefono());
            ps.setInt(5, emp.getRolEmpleadoInt());
            ps.setString(6, emp.getUsuario());
            ps.setString(7, pass);
            ps.setInt(8, emp.getId_empleado());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Eliminar empleado por ID ===
    public boolean eliminar(int idEmpleado) {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === Obtener nombres de sucursales ===
    public static List<String> obtenerNombresSucursales() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM sucursal ORDER BY nombre";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("nombre"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // === Obtener roles de empleados ===
    public List<RolEmpleado> obtenerRoles() {
        List<RolEmpleado> roles = new ArrayList<>();
        String sql = "SELECT id_rol_empleado, nombre_rol FROM rol_empleado ORDER BY nombre_rol";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new RolEmpleado(rs.getInt("id_rol_empleado"), rs.getString("nombre_rol")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }

    // === Obtener ID de sucursal por nombre ===
    public static int obtenerIdSucursalPorNombre(String nombreSucursal) {
        String sql = "SELECT id_sucursal FROM sucursal WHERE nombre = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nombreSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_sucursal");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // devuelve -1 si no se encuentra
    }

    // === Obtener empleado por ID ===
    public Empleado obtenerPorId(int idEmpleado) {
        String sql = """
            SELECT e.id_empleado, e.id_sucursal, e.nombre, e.correo, e.telefono, 
                   e.rol AS rol_id, e.usuario, e.passwrd, 
                   r.nombre_rol AS nombre_rol, s.nombre AS nombre_sucursal
            FROM empleado e
            JOIN rol_empleado r ON e.rol = r.id_rol_empleado
            JOIN sucursal s ON e.id_sucursal = s.id_sucursal
            WHERE e.id_empleado = ?
        """;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empleado emp = new Empleado(
                            rs.getInt("id_empleado"),
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getInt("telefono"),
                            rs.getInt("rol_id"),
                            rs.getString("usuario"),
                            rs.getString("passwrd")
                    );
                    emp.setNombreRol(rs.getString("nombre_rol"));
                    emp.setNombreSucursal(rs.getString("nombre_sucursal"));
                    return emp;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Si no se encuentra el empleado
    }

}
