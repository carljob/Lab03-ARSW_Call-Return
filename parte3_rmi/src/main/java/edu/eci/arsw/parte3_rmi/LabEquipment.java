package edu.eci.arsw.parte3_rmi;

import java.io.Serializable;

public class LabEquipment implements Serializable {
    private String codigo;
    private String nombre;
    private String laboratorio;
    private boolean disponible;

    public LabEquipment(String codigo, String nombre, String laboratorio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.disponible = true;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getLaboratorio() { return laboratorio; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + laboratorio + " | " + (disponible ? "DISPONIBLE" : "RESERVADO");
    }
}