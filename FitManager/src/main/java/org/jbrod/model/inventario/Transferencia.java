package org.jbrod.model.inventario;

import java.sql.Timestamp;

public class Transferencia {

    private int id_transferencia;
    private int id_equipo;

    private int desde_sucursal;
    private int hacia_sucursal;

    private int cantidad;

    private Timestamp fecha;





    public Transferencia(int id_transferencia, int id_equipo, int desde_sucursal, int hacia_sucursal, int cantidad, Timestamp fecha) {
        this.id_transferencia = id_transferencia;
        this.id_equipo = id_equipo;
        this.desde_sucursal = desde_sucursal;
        this.hacia_sucursal = hacia_sucursal;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }






    public int getId_transferencia() {
        return id_transferencia;
    }

    public void setId_transferencia(int id_transferencia) {
        this.id_transferencia = id_transferencia;
    }

    public int getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    public int getDesde_sucursal() {
        return desde_sucursal;
    }

    public void setDesde_sucursal(int desde_sucursal) {
        this.desde_sucursal = desde_sucursal;
    }

    public int getHacia_sucursal() {
        return hacia_sucursal;
    }

    public void setHacia_sucursal(int hacia_sucursal) {
        this.hacia_sucursal = hacia_sucursal;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
