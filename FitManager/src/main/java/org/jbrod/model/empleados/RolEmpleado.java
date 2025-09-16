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


}
