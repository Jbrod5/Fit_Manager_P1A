package org.jbrod.model.clientes_membresias;

import java.time.LocalDate;

public class MembresiaCliente {

    private int idMembresiaCliente;
    private int idCliente;
    private String nombreCliente;
    private String correoCliente;
    private int idTipoMembresia;
    private String nombreTipoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public MembresiaCliente(int idMembresiaCliente, int idCliente, String nombreCliente, String correoCliente,
                            int idTipoMembresia, String nombreTipoMembresia, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idMembresiaCliente = idMembresiaCliente;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.idTipoMembresia = idTipoMembresia;
        this.nombreTipoMembresia = nombreTipoMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdMembresiaCliente() { return idMembresiaCliente; }
    public int getIdCliente() { return idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public String getCorreoCliente() { return correoCliente; }
    public int getIdTipoMembresia() { return idTipoMembresia; }
    public String getNombreTipoMembresia() { return nombreTipoMembresia; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }

    public boolean isActiva() {
        LocalDate hoy = LocalDate.now();
        return (hoy.isEqual(fechaInicio) || hoy.isAfter(fechaInicio)) &&
                (hoy.isBefore(fechaFin) || hoy.isEqual(fechaFin));
    }
}
