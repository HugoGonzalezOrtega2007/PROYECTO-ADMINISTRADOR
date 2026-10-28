package org.digitech.filevault.dao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO<T extends Serializable> {

    private final Path ruta;

    public AlumnoDAO(String rutaFichero) {
        this.ruta = Paths.get(rutaFichero);
    }

    public void guardar(List<T> elementos) throws IOException {
        Path padre = ruta.getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(ruta))) {
            oos.writeObject(new ArrayList<>(elementos));
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> recuperar() throws IOException {
        if (!Files.exists(ruta)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(ruta))) {
            return (List<T>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Clase no encontrada al deserializar " + ruta + ": " + e.getMessage(), e);
        }
    }
}