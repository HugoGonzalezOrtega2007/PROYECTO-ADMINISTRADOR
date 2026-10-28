package org.digitech.filevault.model;

import java.io.Serializable;

public class Alumno implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String email;
    private int faltas;

    public Alumno(int id, String nombre, String email, int faltas) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.faltas = faltas;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getFaltas() { return faltas; }
    public void setFaltas(int faltas) { this.faltas = faltas; }

    @Override
    public String toString() {
        return "Alumno{id=" + id + ", nombre='" + nombre + "', email='" + email + "', faltas=" + faltas + "}";
    }
}
