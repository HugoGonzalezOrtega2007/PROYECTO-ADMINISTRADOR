package org.digitech.filevault.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Matricula implements Serializable {
    private static final long serialVersionUID = 1L;

    private int alumnoId;
    private int cursoId;
    private LocalDate fecha; // LocalDate ya es Serializable

    public Matricula(int alumnoId, int cursoId, LocalDate fecha) {
        this.alumnoId = alumnoId;
        this.cursoId = cursoId;
        this.fecha = fecha;
    }

    public int getAlumnoId() { return alumnoId; }
    public void setAlumnoId(int alumnoId) { this.alumnoId = alumnoId; }
    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return "Matricula{alumnoId=" + alumnoId + ", cursoId=" + cursoId + ", fecha=" + fecha + "}";
    }
}
