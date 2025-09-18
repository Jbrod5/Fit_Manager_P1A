package org.jbrod.model.empleados;

public class RolEmpleado {

    private int idRolEmpleado;
    private String nombreRol;

    public RolEmpleado(int idRolEmpleado, String nombreRol) {
        this.idRolEmpleado = idRolEmpleado;
        this.nombreRol = nombreRol;
    }

    public int getIdRolEmpleado() {
        return idRolEmpleado;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    @Override
    public String toString() {
        // esto hace que JComboBox muestre el nombre automáticamente
        return nombreRol;
    }

    // (opcional) equals/hashCode por id si quieres comparar objetos directamente
}
