package org.jbrod.model.sucursales_equipos;

public class Sucursal {

    private int idSucursal;
    private TipoSucursal tipoSucursal;

    private String nombre;
    private String direccion;
    private int cantidadMaquinas;

    public Sucursal(int idSucursal, TipoSucursal tipoSucursal, String nombre, String direccion) {
        this.idSucursal = idSucursal;
        this.tipoSucursal = tipoSucursal;
        this.nombre = nombre;
        this.direccion = direccion;
    }





    public void setCantidadMaquinas(int cantidadMaquinas) {
        this.cantidadMaquinas = cantidadMaquinas;
    }


    public void agreagarMaquinas(int cantidadAAgregar){
        cantidadMaquinas = cantidadMaquinas += cantidadAAgregar;
    }

    public void quitarMaquinas(int cantidadAQuitars){
        if((cantidadMaquinas - cantidadAQuitars) >0){
            cantidadMaquinas = cantidadMaquinas - cantidadAQuitars;
        }else{
            //quizá lanzar una excepcion?
        }
    }




    public int getIdSucursal() {
        return idSucursal;
    }

    public TipoSucursal getTipoSucursal() {
        return tipoSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getCantidadMaquinas() {
        return cantidadMaquinas;
    }




}
