package org.jbrod.model.pagos_asistencias;

import java.time.LocalDate;

public class Pago {
    private int idPago;
    private int idCliente;
    private String nombreCliente;
    private String correoCliente;
    private int idMembresia; // puede ser null si no aplica
    private double monto;
    private LocalDate fechaPago;
    private String tipoServicio;
    private String descripcion;

    public Pago(int idPago, int idCliente, String nombreCliente, String correoCliente,
                int idMembresia, double monto, LocalDate fechaPago,
                String tipoServicio, String descripcion) {
        this.idPago = idPago;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.idMembresia = idMembresia;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
    }

    // Getters
    public int getIdPago() { return idPago; }
    public int getIdCliente() { return idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public int getIdMembresia() { return idMembresia; }
    public double getMonto() { return monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public String getTipoServicio() { return tipoServicio; }
    public String getDescripcion() { return descripcion; }
}
