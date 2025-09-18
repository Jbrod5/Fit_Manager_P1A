package org.jbrod.model.inventario;

public class Inventario {

    private int id_inventario;
    private int id_equipo;
    private int id_sucursal;
    private int cantidad;


    public Inventario(int id_inventario, int id_equipo, int id_sucursal, int cantidad) {
        this.id_inventario = id_inventario;
        this.id_equipo = id_equipo;
        this.id_sucursal = id_sucursal;
        this.cantidad = cantidad;
    }


    public int getId_inventario() {
        return id_inventario;
    }

    public void setId_inventario(int id_inventario) {
        this.id_inventario = id_inventario;
    }

    public int getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    public int getId_sucursal() {
        return id_sucursal;
    }

    public void setId_sucursal(int id_sucursal) {
        this.id_sucursal = id_sucursal;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
