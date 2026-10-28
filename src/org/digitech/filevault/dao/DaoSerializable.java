package org.digitech.filevault.dao;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DaoSerializable<T extends Serializable> {

    private final Path ruta;

    public DaoSerializable(String rutaFichero) {
        this.ruta = Paths.get(rutaFichero);
    }

    public void guardar(List<T> elementos) throws IOException {
        Files.createDirectories(ruta.getParent());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta.toFile()))) {
            oos.writeObject(new ArrayList<>(elementos));
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> recuperar() throws IOException {
        if (!Files.exists(ruta)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta.toFile()))) {
            return (List<T>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Clase no encontrada al deserializar " + ruta + ": " + e.getMessage(), e);
        }
    }
}
