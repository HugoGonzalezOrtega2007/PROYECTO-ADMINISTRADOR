package org.digitech.filevault.dao;

import org.digitech.filevault.model.Alumno;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AccesoDirectoAlumnos {

    private static final Path DIR = Paths.get("colegio", "alumnos");
    private static final Path FICHERO = DIR.resolve("alumnos_directo.dat");

    private static final int NOMBRE_CHARS = 24;
    private static final int EMAIL_CHARS = 22;

    private static final int TAM_REGISTRO = 4 + NOMBRE_CHARS * 2 + EMAIL_CHARS * 2 + 4; // 100

    private static final int OFFSET_FALTAS = 4 + NOMBRE_CHARS * 2 + EMAIL_CHARS * 2;    // 96

    public static void guardarTodos(List<Alumno> alumnos) {
        try {
            Files.createDirectories(DIR);

            try (RandomAccessFile raf = new RandomAccessFile(FICHERO.toFile(), "rw")) {
                raf.setLength(0);
                for (Alumno a : alumnos) {
                    escribirRegistro(raf, a);
                }
            }
            System.out.println("Alumnos guardados en acceso directo: " + alumnos.size());
        } catch (IOException e) {
            System.err.println("Error al guardar los alumnos: " + e.getMessage());
        }
    }

    public static List<Alumno> leerTodos() {
        List<Alumno> alumnos = new ArrayList<>();

        if (!Files.exists(FICHERO)) {
            System.err.println("El fichero de acceso directo todavía no existe: " + FICHERO);
            return alumnos;
        }

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO.toFile(), "r")) {
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                alumnos.add(leerRegistro(raf));
            }
        } catch (IOException e) {
            System.err.println("Error al leer los alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    public static Alumno leerPorId(int id) {
        if (!Files.exists(FICHERO)) {
            return null;
        }

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO.toFile(), "r")) {
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                long pos = i * TAM_REGISTRO;
                raf.seek(pos);
                if (raf.readInt() == id) {
                    raf.seek(pos);
                    return leerRegistro(raf);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al buscar el alumno: " + e.getMessage());
        }

        return null;
    }

    public static boolean modificarFaltas(int id, int nuevasFaltas) {
        if (!Files.exists(FICHERO)) {
            System.err.println("El fichero de acceso directo todavía no existe: " + FICHERO);
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO.toFile(), "rw")) {
            long total = raf.length() / TAM_REGISTRO;
            for (long i = 0; i < total; i++) {
                long pos = i * TAM_REGISTRO;
                raf.seek(pos);
                if (raf.readInt() == id) {
                    raf.seek(pos + OFFSET_FALTAS);
                    raf.writeInt(nuevasFaltas);
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al modificar las faltas: " + e.getMessage());
        }

        return false;
    }

    private static void escribirRegistro(RandomAccessFile raf, Alumno alumno) throws IOException {
        raf.writeInt(alumno.getId());
        escribirTexto(raf, alumno.getNombre(), NOMBRE_CHARS);
        escribirTexto(raf, alumno.getEmail(), EMAIL_CHARS);
        raf.writeInt(alumno.getFaltas());
    }

    private static Alumno leerRegistro(RandomAccessFile raf) throws IOException {
        int id = raf.readInt();
        String nombre = leerTexto(raf, NOMBRE_CHARS);
        String email = leerTexto(raf, EMAIL_CHARS);
        int faltas = raf.readInt();
        double nota = 0.0;
        return new Alumno(id, nombre, email, faltas, nota);
    }

    private static void escribirTexto(RandomAccessFile raf, String texto, int longitud) throws IOException {
        String t = (texto == null) ? "" : texto;
        for (int i = 0; i < longitud; i++) {
            raf.writeChar(i < t.length() ? t.charAt(i) : ' ');
        }
    }


    private static String leerTexto(RandomAccessFile raf, int longitud) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append(raf.readChar());
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        guardarTodos(List.of(
                new Alumno(1, "Marta Puig", "marta@instituto.cat", 2, 7.0),
                new Alumno(2, "Pau Serra", "pau@instituto.cat", 0, 8.3)
        ));

        System.out.println("\n--- Antes ---");
        leerTodos().forEach(System.out::println);

        boolean ok = modificarFaltas(2, 7);
        System.out.println("\nModificación realizada: " + ok);

        System.out.println("\n--- Después (alumno 2) ---");
        System.out.println(leerPorId(2));
    }
}