package edu.eci.arsw.parte2_http;

public class Salon {
    private String id;
    private boolean disponible;

    public Salon(String id) {
        this.id = id;
        this.disponible = true;
    }

    public String getId() { return id; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String toText() {
        return id + "," + (disponible ? "DISPONIBLE" : "RESERVADO");
    }
}