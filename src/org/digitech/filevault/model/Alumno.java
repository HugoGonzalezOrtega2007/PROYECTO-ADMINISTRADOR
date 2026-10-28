package org.digitech.filevault.model;

import java.io.Serializable;

public class Alumno implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String email;
    private int faltas;
    private double nota;

    public Alumno(int id, String nombre, String email, int faltas, double nota) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.faltas = faltas;
        this.nota = nota;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getFaltas() { return faltas; }
    public void setFaltas(int faltas) { this.faltas = faltas; }
    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }

    @Override
    public String toString() {
        return "Id Alumno " + id + " -- Nombre: " + nombre + " -- Email: " + email + " -- Faltas: " + faltas + " -- Nota Media: " + nota;
    }
}
