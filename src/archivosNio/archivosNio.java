package archivosNio;

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
import java.util.Iterator;
import java.util.List;

public class archivosNio {

    private static final Path BASE = Paths.get("colegio");
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void crearEstructura() {
        String[] carpetas = {"alumnos", "cursos", "matriculas"};
        for (int i = 0; i < carpetas.length; i++) {
            Path dir = BASE.resolve(carpetas[i]);
            try {
                if (Files.exists(dir)) {
                    System.out.println("Ya existe: " + dir);
                } else {
                    Files.createDirectories(dir);
                    System.out.println("Creada: " + dir);
                }
            } catch (IOException e) {
                System.err.println("No se pudo crear " + dir + ": " + e.getMessage());
            }
        }
    }

    public static void listarDirectorio(Path dir) {
        if (!Files.exists(dir)) {
            System.err.println("Error: el directorio no existe -> " + dir);
            return;
        }
        if (!Files.isDirectory(dir)) {
            System.err.println("Error: la ruta no es un directorio -> " + dir);
            return;
        }

        List<Path> entradas = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Iterator<Path> it = stream.iterator(); it.hasNext(); ) {
                entradas.add(it.next());
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

        entradas.sort(Comparator.comparing(
                p -> p.getFileName().toString(), String.CASE_INSENSITIVE_ORDER));

        System.out.printf("%-30s %12s %-20s %-8s%n",
                "NOMBRE", "TAMAÑO (KB)", "MODIFICADO", "PERMISOS");
        System.out.println("-".repeat(75));

        for (int i = 0; i < entradas.size(); i++) {
            Path p = entradas.get(i);
            try {
                BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);

                String nombre = p.getFileName().toString();
                if (attr.isDirectory()) {
                    nombre += "/";
                }

                double kb = attr.size() / 1024.0;

                LocalDateTime fecha = LocalDateTime.ofInstant(
                        attr.lastModifiedTime().toInstant(), ZoneId.systemDefault());

                String permisos = (Files.isReadable(p) ? "R" : "-")
                        + (Files.isWritable(p) ? "W" : "-")
                        + (Files.isExecutable(p) ? "X" : "-");

                System.out.printf("%-30s %12.2f %-20s %-8s%n",
                        nombre, kb, fecha.format(FORMATO), permisos);

            } catch (IOException e) {
                System.err.println("No se pudo leer " + p.getFileName() + ": " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        crearEstructura();
        System.out.println();
        listarDirectorio(BASE.resolve("alumnos"));
    }
}