package org.jbrod.model.clientes_membresias;


import java.sql.Date;

public class EntrenadorAsignacionHistorial {

    private int idCliente;
    private String nombreCliente;
    private String correoCliente;

    private int idEntrenador;
    private String nombreEntrenador;
    private String correoEntrenador;

    private Date fechaAsignacion;
    private String nombreSucursal;


    public EntrenadorAsignacionHistorial(int idCliente, String nombreCliente, String correoCliente,
                                         int idEntrenador, String nombreEntrenador, String correoEntrenador,
                                         java.sql.Date fechaAsignacion, String nombreSucursal) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.idEntrenador = idEntrenador;
        this.nombreEntrenador = nombreEntrenador;
        this.correoEntrenador = correoEntrenador;
        this.fechaAsignacion = fechaAsignacion;
        this.nombreSucursal = nombreSucursal;
    }


    // Getters
    public int getIdCliente() { return idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public int getIdEntrenador() { return idEntrenador; }
    public String getNombreEntrenador() { return nombreEntrenador; }
    public String getCorreoEntrenador() { return correoEntrenador; }
    public Date getFechaAsignacion() { return fechaAsignacion; }
    public String getNombreSucursal() {
        return nombreSucursal;
    }
}
