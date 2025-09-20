package org.jbrod.model.clientes_membresias;

import java.sql.Timestamp;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String correo;
    private Timestamp fechaRegistro;

    // === Constructores ===
    public Cliente() {}

    public Cliente(int idCliente, String nombre, String correo, Timestamp fechaRegistro) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
    }

    // === Getters y Setters ===
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}