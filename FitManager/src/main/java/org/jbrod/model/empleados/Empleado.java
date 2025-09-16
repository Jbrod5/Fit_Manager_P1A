package org.jbrod.model.empleados;

public class Empleado {

    private int id_empleado;
    private int id_sucursal;

    private String nombre;
    private String correo;
    private int telefono;

    private RolEmpleado rolEmpleado; //Aquí manejare el rol y el id del rol juntos
    private int rolEmpleadoInt;

    private String usuario;

    private String password;


    public Empleado(int id_empleado, int id_sucursal, String nombre, String correo, int telefono, RolEmpleado rolEmpleado, String usuario, String password) {
        this.id_empleado = id_empleado;
        this.id_sucursal = id_sucursal;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rolEmpleado = rolEmpleado;
        this.usuario = usuario;
        this.password = password;
    }

    public Empleado(int id_empleado, int id_sucursal, String nombre, String correo, int telefono, int rolEmpleado, String usuario, String password) {
        this.id_empleado = id_empleado;
        this.id_sucursal = id_sucursal;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rolEmpleadoInt = rolEmpleado;
        this.usuario = usuario;
        this.password = password;
    }





    public int getId_empleado() {
        return id_empleado;
    }

    public int getId_sucursal() {
        return id_sucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public int getTelefono() {
        return telefono;
    }

    public RolEmpleado getRolEmpleado() {
        return rolEmpleado;
    }

    public int getRolEmpleadoInt() {
        return rolEmpleadoInt;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }



}
