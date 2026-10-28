package org.digitech.filevault.util;

import org.digitech.filevault.model.Alumno;
import org.digitech.filevault.model.Curso;
import org.digitech.filevault.model.Matricula;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;


public class CsvExportador {

    private static final Path DIR = Paths.get("./data/backup");
    private static final Path ALUMNOS = DIR.resolve("alumnos.csv");
    private static final Path CURSOS = DIR.resolve("cursos.csv");
    private static final Path MATRICULAS = DIR.resolve("matriculas.csv");

    public static Path getDirectorio() {
        return DIR;
    }

    public static void exportarAlumnos(List<Alumno> alumnos) throws IOException {
        Files.createDirectories(DIR);
        try (BufferedWriter bw = Files.newBufferedWriter(ALUMNOS, StandardCharsets.UTF_8)) {
            bw.write("id,nombre,email,faltas,nota");
            bw.newLine();
            for (Alumno a : alumnos) {
                bw.write(a.getId() + "," + escapar(a.getNombre()) + ","
                        + escapar(a.getEmail()) + "," + a.getFaltas() + ","
                        + String.format(Locale.ROOT, "%.2f", a.getNota()));
                bw.newLine();
            }
        }
    }

    public static void exportarCursos(List<Curso> cursos) throws IOException {
        Files.createDirectories(DIR);
        try (BufferedWriter bw = Files.newBufferedWriter(CURSOS, StandardCharsets.UTF_8)) {
            bw.write("id,nombre,horas");
            bw.newLine();
            for (Curso c : cursos) {
                bw.write(c.getId() + "," + escapar(c.getNombre()) + "," + c.getHoras());
                bw.newLine();
            }
        }
    }

    public static void exportarMatriculas(List<Matricula> matriculas) throws IOException {
        Files.createDirectories(DIR);
        try (BufferedWriter bw = Files.newBufferedWriter(MATRICULAS, StandardCharsets.UTF_8)) {
            bw.write("alumno_id,curso_id,fecha");
            bw.newLine();
            for (Matricula m : matriculas) {
                bw.write(m.getAlumnoId() + "," + m.getCursoId() + "," + m.getFecha());
                bw.newLine();
            }
        }
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
