package org.jbrod.model.sucursales_equipos;

public class TipoSucursal {

    private int idTipoSucursal;
    private  String nombreTipo;

    public TipoSucursal(int idTipoSucursal, String nombreTipo) {
        this.idTipoSucursal = idTipoSucursal;
        this.nombreTipo = nombreTipo;
    }

    public int getIdTipoSucursal() {
        return idTipoSucursal;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }
}
