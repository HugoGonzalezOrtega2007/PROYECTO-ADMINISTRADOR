package colegio.archivosNio;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class archivosNio {

    private static final Path BASE = Paths.get("empresa");
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ---------- 1. Crear estructura ----------
    public static void crearEstructura() {
        String[] carpetas = {"auditoria", "empleados", "logs"};
        for (String nombre : carpetas) {
            Path dir = BASE.resolve(nombre);
            try {
                if (Files.exists(dir)) {
                    System.out.println("Ya existe: " + dir);
                } else {
                    Files.createDirectories(dir);   // crea también "empresa" si falta
                    System.out.println("Creada: " + dir);
                }
            } catch (IOException e) {
                System.err.println("No se pudo crear " + dir + ": " + e.getMessage());
            }
        }
    }

    // ---------- 2. Listar directorio ----------
    public static void listarDirectorio(Path dir) {
        // Validaciones previas
        if (!Files.exists(dir)) {
            System.err.println("Error: el directorio no existe -> " + dir);
            return;
        }
        if (!Files.isDirectory(dir)) {
            System.err.println("Error: la ruta no es un directorio -> " + dir);
            return;
        }

        List<Path> entradas = new ArrayList<>();

        // try-with-resources: el DirectoryStream se cierra solo
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path p : stream) {
                entradas.add(p);
            }
        } catch (NoSuchFileException e) {
            System.err.println("Error: no existe -> " + e.getMessage());
            return;
        } catch (IOException | SecurityException e) {
            System.err.println("Error leyendo el directorio (¿permisos?): " + e.getMessage());
            return;
        }

        if (entradas.isEmpty()) {
            System.out.println("(directorio vacío)");
            return;
        }

        // Orden alfabético
        entradas.sort(Comparator.comparing(
                p -> p.getFileName().toString(), String.CASE_INSENSITIVE_ORDER));

        System.out.printf("%-30s %12s %-20s %-8s%n",
                "NOMBRE", "TAMAÑO (KB)", "MODIFICADO", "PERMISOS");
        System.out.println("-".repeat(75));

        for (Path p : entradas) {
            try {
                BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);

                String nombre = p.getFileName().toString();
                if (attr.isDirectory()) nombre += "/";

                double kb = attr.size() / 1024.0;

                LocalDateTime fecha = LocalDateTime.ofInstant(
                        attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());

                String permisos = (Files.isReadable(p) ? "R" : "-")
                        + (Files.isWritable(p) ? "W" : "-")
                        + (Files.isExecutable(p) ? "X" : "-");

                System.out.printf("%-30s %12.2f %-20s %-8s%n",
                        nombre, kb, fecha.format(FORMATO), permisos);

            } catch (IOException e) {
                // Un archivo problemático no debe romper todo el listado
                System.err.println("No se pudo leer " + p.getFileName() + ": " + e.getMessage());
            }
        }
    }

    // ---------- Prueba ----------
    public static void main(String[] args) {
        crearEstructura();
        System.out.println();
        listarDirectorio(BASE.resolve("auditoria"));
    }
}