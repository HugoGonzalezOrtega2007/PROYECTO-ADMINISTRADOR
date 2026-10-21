package org.digitech.filevault.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLogger {

    public enum Nivel { INFO, WARNING, ERROR }

    private static final Path LOG_DIR = Paths.get("./data/logs");
    private static final Path LOG_FILE = LOG_DIR.resolve("audit.log");
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Crea ./data/logs si no existe
    private static void asegurarDirectorio() throws IOException {
        if (!Files.exists(LOG_DIR)) {
            Files.createDirectories(LOG_DIR);
        }
    }

    // Escribe una línea y data fecha con año mes y dia: [YYYY-MM-DD HH:mm:ss]
    public static void registrar(Nivel nivel, String descripcion) {
        try {
            asegurarDirectorio();

            try (BufferedWriter bw = Files.newBufferedWriter(
                    LOG_FILE,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                String linea = "[" + LocalDateTime.now().format(FORMATO) + "] ["
                        + nivel + "] " + descripcion;
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo escribir en el log: " + e.getMessage());
        }
    }

    // Busca una palabra clave (sin distinguir mayúsculas) y devuelve las líneas que la contienen
    public static List<String> buscar(String palabraClave) {
        List<String> resultados = new ArrayList<>();

        if (!Files.exists(LOG_FILE)) {
            System.err.println("El log todavía no existe: " + LOG_FILE);
            return resultados;
        }

        try (BufferedReader br = Files.newBufferedReader(LOG_FILE, StandardCharsets.UTF_8)) {
            String linea;
            String clave = palabraClave.toLowerCase();

            while ((linea = br.readLine()) != null) {
                if (linea.toLowerCase().contains(clave)) {
                    resultados.add(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer el log: " + e.getMessage());
        }

        return resultados;
    }

    // Prueba rápida
    public static void main(String[] args) {
        registrar(Nivel.INFO, "Aplicación iniciada");
        registrar(Nivel.WARNING, "Archivo empleados.csv sin permisos de escritura");
        registrar(Nivel.ERROR, "Fallo al serializar Empleado");

        List<String> encontradas = buscar("error");
        for (int i = 0; i < encontradas.size(); i++) {
            System.out.println(encontradas.get(i));
        }
    }
}
