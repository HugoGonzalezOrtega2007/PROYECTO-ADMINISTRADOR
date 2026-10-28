package org.digitech.filevault.app;

import org.digitech.filevault.dao.AccesoDirectoAlumnos;
import org.digitech.filevault.dao.AlumnoDAO;
import org.digitech.filevault.model.Alumno;
import org.digitech.filevault.model.Curso;
import org.digitech.filevault.model.Matricula;
import org.digitech.filevault.util.CsvExportador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainApp {

    private static final Scanner SC = new Scanner(System.in);


    private static final AlumnoDAO<Alumno> ALUMNOS_DAO =
            new AlumnoDAO<>("colegio/alumnos/alumnos.dat");
    private static final AlumnoDAO<Curso> CURSOS_DAO =
            new AlumnoDAO<>("colegio/cursos/cursos.dat");
    private static final AlumnoDAO<Matricula> MATRICULAS_DAO =
            new AlumnoDAO<>("colegio/matriculas/matriculas.dat");

    private static List<Alumno> alumnos = new ArrayList<>();
    private static List<Curso> cursos = new ArrayList<>();
    private static List<Matricula> matriculas = new ArrayList<>();


    public static void main(String[] args) {
        cargarDatos();

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
                case 1 -> anadirAlumno();
                case 2 -> listar("ALUMNOS", alumnos);
                case 3 -> anadirCurso();
                case 4 -> listar("CURSOS", cursos);
                case 5 -> matricular();
                case 6 -> listar("MATRÍCULAS", matriculas);
                case 7 -> registrarFalta();
                case 8 -> eliminarAlumno();
                case 9 -> guardarDatos();
                case 10 -> exportarCsv();
                case 11 -> verificarCsv();
                case 12 -> AccesoDirectoAlumnos.guardarTodos(alumnos);
                case 13 -> consultarDirecto();
                case 14 -> modificarFaltasDirecto();
                case 15 -> modificarNotaDirecto();
                case 0 -> {
                    guardarDatos();
                    System.out.println("Hasta pronto.");
                }
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }


    private static void mostrarMenu() {
        System.out.println("\n===== GESTIÓN DEL INSTITUTO =====");
        System.out.println(" 1. Añadir alumno");
        System.out.println(" 2. Listar alumnos");
        System.out.println(" 3. Añadir curso");
        System.out.println(" 4. Listar cursos");
        System.out.println(" 5. Matricular alumno en curso");
        System.out.println(" 6. Listar matrículas");
        System.out.println(" 7. Registrar falta a un alumno");
        System.out.println(" 8. Eliminar alumno (y sus matrículas)");
        System.out.println(" 9. Guardar datos (.dat)");
        System.out.println("10. Exportar a CSV");
        System.out.println("11. Verificar CSV exportados");
        System.out.println("--- Acceso directo (RandomAccessFile) ---");
        System.out.println("12. Volcar alumnos al fichero de acceso directo");
        System.out.println("13. Consultar alumno por id");
        System.out.println("14. Modificar faltas de un alumno");
        System.out.println("15. Modificar nota de un alumno");
        System.out.println(" 0. Salir");
    }


    private static void cargarDatos() {
        try {
            alumnos = new ArrayList<>(ALUMNOS_DAO.recuperar());
            cursos = new ArrayList<>(CURSOS_DAO.recuperar());
            matriculas = new ArrayList<>(MATRICULAS_DAO.recuperar());
            System.out.println("Datos cargados: " + alumnos.size() + " alumnos, "
                    + cursos.size() + " cursos, " + matriculas.size() + " matrículas.");
        } catch (IOException e) {
            System.err.println("Error al cargar los datos (es posible que los ficheros no existan todavía): " + e.getMessage());
        }
    }


    private static void guardarDatos() {
        try {
            ALUMNOS_DAO.guardar(alumnos);
            CURSOS_DAO.guardar(cursos);
            MATRICULAS_DAO.guardar(matriculas);
            System.out.println("Datos guardados correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }


    private static void anadirAlumno() {
        int id = leerEntero("Id del alumno: ");
        if (buscarAlumno(id) != null) {
            System.out.println("Ya existe un alumno con ese id.");
            return;
        }
        System.out.print("Nombre: ");
        String nombre = SC.nextLine();
        System.out.print("Email: ");
        String email = SC.nextLine();
        double nota = leerNota("Nota (0-10): ");

        alumnos.add(new Alumno(id, nombre, email, 0, nota));
        System.out.println("Alumno añadido.");
    }


    private static void anadirCurso() {
        int id = leerEntero("Id del curso: ");
        if (buscarCurso(id) != null) {
            System.out.println("Ya existe un curso con ese id.");
            return;
        }
        System.out.print("Nombre: ");
        String nombre = SC.nextLine();
        int horas = leerEntero("Horas: ");

        cursos.add(new Curso(id, nombre, horas));
        System.out.println("Curso añadido.");
    }


    private static void matricular() {
        int alumnoId = leerEntero("Id del alumno: ");
        int cursoId = leerEntero("Id del curso: ");

        if (buscarAlumno(alumnoId) == null || buscarCurso(cursoId) == null) {
            System.out.println("El alumno o el curso no existen.");
            return;
        }
        for (Matricula m : matriculas) {
            if (m.getAlumnoId() == alumnoId && m.getCursoId() == cursoId) {
                System.out.println("Ese alumno ya está matriculado en el curso.");
                return;
            }
        }

        LocalDate fecha = leerFecha("Fecha (AAAA-MM-DD, vacío = hoy): ");
        matriculas.add(new Matricula(alumnoId, cursoId, fecha));
        System.out.println("Matrícula registrada.");
    }


    private static void registrarFalta() {
        Alumno a = buscarAlumno(leerEntero("Id del alumno: "));
        if (a == null) {
            System.out.println("No se encontró el alumno.");
            return;
        }
        a.setFaltas(a.getFaltas() + 1);
        System.out.println("Faltas de " + a.getNombre() + ": " + a.getFaltas());
    }


    private static void eliminarAlumno() {
        int id = leerEntero("Id del alumno a eliminar: ");
        Alumno a = buscarAlumno(id);
        if (a == null) {
            System.out.println("No se encontró el alumno.");
            return;
        }
        alumnos.remove(a);
        matriculas.removeIf(m -> m.getAlumnoId() == id);
        System.out.println("Alumno y sus matrículas eliminados.");
    }


    private static void exportarCsv() {
        try {
            CsvExportador.exportarAlumnos(alumnos);
            CsvExportador.exportarCursos(cursos);
            CsvExportador.exportarMatriculas(matriculas);
            System.out.println("CSV exportados en: " + CsvExportador.getDirectorio());
        } catch (IOException e) {
            System.err.println("Error al exportar los CSV: " + e.getMessage());
        }
    }

    private static void verificarCsv() {
        for (String nombre : List.of("alumnos.csv", "cursos.csv", "matriculas.csv")) {
            Path fichero = CsvExportador.getDirectorio().resolve(nombre);
            System.out.println("\n--- " + nombre + " ---");
            try {
                if (!Files.exists(fichero)) {
                    System.out.println("No existe todavía (usa la opción 10).");
                    continue;
                }
                Files.readAllLines(fichero).forEach(System.out::println);
            } catch (IOException e) {
                System.err.println("Error al leer " + nombre + ": " + e.getMessage());
            }
        }
    }


    private static void consultarDirecto() {
        Alumno a = AccesoDirectoAlumnos.leerPorId(leerEntero("Id del alumno: "));
        System.out.println(a != null ? a
                : "No encontrado (¿has volcado los alumnos con la opción 12?).");
    }

    private static void modificarFaltasDirecto() {
        int id = leerEntero("Id del alumno: ");
        int faltas = leerEntero("Nuevas faltas: ");

        if (AccesoDirectoAlumnos.modificarFaltas(id, faltas)) {
            Alumno a = buscarAlumno(id);
            if (a != null) {
                a.setFaltas(faltas);
            }
            System.out.println("Faltas modificadas por acceso directo.");
        } else {
            System.out.println("No se pudo modificar (¿existe el alumno y has hecho la opción 12?).");
        }
    }

    private static void modificarNotaDirecto() {
        int id = leerEntero("Id del alumno: ");
        double nota = leerNota("Nueva nota (0-10): ");

        if (AccesoDirectoAlumnos.modificarNotas(id, nota)) {
            Alumno a = buscarAlumno(id);
            if (a != null) {
                a.setNota(nota);
            }
            System.out.println("Nota modificada por acceso directo.");
        } else {
            System.out.println("No se pudo modificar (¿existe el alumno y has hecho la opción 12?).");
        }
    }


    private static <T> void listar(String titulo, List<T> lista) {
        System.out.println("\n--- " + titulo + " ---");
        if (lista.isEmpty()) {
            System.out.println("(vacío)");
            return;
        }
        lista.forEach(System.out::println);
    }


    private static Alumno buscarAlumno(int id) {
        for (Alumno a : alumnos) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }


    private static Curso buscarCurso(int id) {
        for (Curso c : cursos) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(SC.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero válido.");
            }
        }
    }

    private static double leerNota(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double nota = Double.parseDouble(SC.nextLine().trim().replace(',', '.'));
                if (nota >= 0 && nota <= 10) {
                    return nota;
                }
                System.out.println("La nota debe estar entre 0 y 10.");
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
            }
        }
    }


    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SC.nextLine().trim();
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Formato no válido. Ejemplo: 2026-09-15");
            }
        }
    }
}
