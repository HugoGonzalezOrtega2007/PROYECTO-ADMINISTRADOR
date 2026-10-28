package org.digitech.filevault.dao;

import org.digitech.filevault.model.Alumno;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDao {

    private static final Path DIR = Paths.get("./data/escuela");
    private static final Path FICHERO = DIR.resolve("alumnos.dat");

    public static void guardar(List<Alumno> alumnos) {
        try {
            if (!Files.exists(DIR)) {
                Files.createDirectories(DIR);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(FICHERO))) {
                oos.writeObject(new ArrayList<>(alumnos));
            }
            System.out.println("Alumnos guardados: " + alumnos.size());
        } catch (IOException e) {
            System.err.println("Error al guardar los alumnos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Alumno> cargar() {
        List<Alumno> alumnos = new ArrayList<>();

        if (!Files.exists(FICHERO)) {
            System.err.println("El fichero de alumnos todavía no existe: " + FICHERO);
            return alumnos;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FICHERO))) {
            alumnos = (List<Alumno>) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("La clase Alumno no se encontró al deserializar: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer los alumnos: " + e.getMessage());
        }

        return alumnos;
    }
}



