package org.digitech.filevault.model;

import java.io.Serializable;

public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private int horas;

    public Curso(int id, String nombre, int horas) {
        this.id = id;
        this.nombre = nombre;
        this.horas = horas;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getHoras() { return horas; }
    public void setHoras(int horas) { this.horas = horas; }

    @Override
    public String toString() {
        return "Curso " + id + " -- Nombre: " + nombre + " -- Horas: " + horas;
    }
}
