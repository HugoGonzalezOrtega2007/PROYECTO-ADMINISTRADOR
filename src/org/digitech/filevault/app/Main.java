package org.digitech.filevault.app;

import org.digitech.filevault.model.*;

import org.digitech.filevault.dao.AlumnoDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AlumnoDAO<Alumno> alumnosDao = new AlumnoDAO<>("colegio/alumnos/alumnos.dat");
        AlumnoDAO<Curso> cursosDao = new AlumnoDAO<>("colegio/cursos/cursos.dat");
        AlumnoDAO<Matricula> matriculasDao = new AlumnoDAO<>("colegio/matriculas/matriculas.dat");

        try {
            alumnosDao.guardar(List.of(
                    new Alumno(1, "Marta Puig", "marta@instituto.cat", 2, 7.0),
                    new Alumno(2, "Pau Serra", "pau@instituto.cat", 0, 8.3)
            ));

            cursosDao.guardar(List.of(
                    new Curso(1, "Programación", 240),
                    new Curso(2, "Bases de Datos", 180)
            ));

            matriculasDao.guardar(List.of(
                    new Matricula(1, 1, LocalDate.of(2026, 9, 15)),
                    new Matricula(2, 2, LocalDate.of(2026, 9, 16))
            ));

            alumnosDao.recuperar().forEach(System.out::println);
            cursosDao.recuperar().forEach(System.out::println);
            matriculasDao.recuperar().forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
}
