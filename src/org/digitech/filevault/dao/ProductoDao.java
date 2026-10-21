package org.digitech.filevault.dao;

import org.digitech.filevault.model.Producto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao{

    private static final Path DIR = Paths.get("./data/productos");
    private static final Path FICHERO = DIR.resolve("inventario.dat");

    // Guarda la lista completa (sobrescribe el fichero)
    public static void guardar(List<Producto> productos) {
        try {
            if (!Files.exists(DIR)) {
                Files.createDirectories(DIR);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(FICHERO))) {
                oos.writeObject(new ArrayList<>(productos));
            }
            System.out.println("Inventario guardado: " + productos.size() + " productos.");
        } catch (IOException e) {
            System.err.println("Error al guardar el inventario: " + e.getMessage());
        }
    }

    // Recupera la lista completa
    @SuppressWarnings("unchecked")
    public static List<Producto> cargar() {
        List<Producto> productos = new ArrayList<>();

        if (!Files.exists(FICHERO)) {
            System.err.println("El inventario todavía no existe: " + FICHERO);
            return productos;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FICHERO))) {
            productos = (List<Producto>) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("La clase Producto no se encontró al deserializar: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer el inventario: " + e.getMessage());
        }

        return productos;
    }

        }

