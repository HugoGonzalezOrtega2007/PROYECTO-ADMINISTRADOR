package cursos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.example.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursosTab extends Tab {

    public static class Curso {
        private final int id;
        private final String nombre;
        private final int horas;

        public Curso(int id, String nombre, int horas) {
            this.id = id; this.nombre = nombre; this.horas = horas;
        }
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public int getHoras() { return horas; }

        @Override
        public String toString() { return nombre; }   // para los ComboBox
    }

    private final ObservableList<Curso> datos = FXCollections.observableArrayList();
    private final TableView<Curso> tabla = new TableView<>(datos);
    private final TextField txtNombre = new TextField();
    private final TextField txtHoras = new TextField("0");

    public CursosTab() {
        super("Cursos");
        setClosable(false);

        tabla.getColumns().add(Db.<Curso>col("ID", "id"));
        tabla.getColumns().add(Db.<Curso>col("Nombre", "nombre"));
        tabla.getColumns().add(Db.<Curso>col("Horas", "horas"));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        txtNombre.setPromptText("Nombre del curso");
        txtHoras.setPrefWidth(70);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel != null) {
                txtNombre.setText(sel.getNombre());
                txtHoras.setText(String.valueOf(sel.getHoras()));
            }
        });

        Button btnAnadir = new Button("Añadir");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");

        btnAnadir.setOnAction(e -> guardar(null));
        btnModificar.setOnAction(e -> {
            Curso sel = tabla.getSelectionModel().getSelectedItem();
            if (sel == null) { Db.aviso("Selecciona un curso de la tabla."); return; }
            guardar(sel.getId());
        });
        btnEliminar.setOnAction(e -> eliminar());
        btnLimpiar.setOnAction(e -> limpiar());

        HBox form = new HBox(10, txtNombre, txtHoras, btnAnadir, btnModificar, btnEliminar, btnLimpiar);
        form.setPadding(new Insets(10));

        BorderPane root = new BorderPane(tabla);
        root.setBottom(form);
        setContent(root);
    }

    public void cargar() {
        try {
            datos.setAll(listar());
        } catch (SQLException ex) {
            Db.aviso("No se pudieron cargar los cursos: " + ex.getMessage());
        }
    }

    public static List<Curso> listar() throws SQLException {
        List<Curso> lista = new ArrayList<>();
        try (Connection c = Db.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nombre, horas FROM cursos ORDER BY id")) {
            while (rs.next()) {
                lista.add(new Curso(rs.getInt("id"), rs.getString("nombre"), rs.getInt("horas")));
            }
        }
        return lista;
    }

    private void guardar(Integer id) {
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) { Db.aviso("El nombre es obligatorio."); return; }
            int horas = Integer.parseInt(txtHoras.getText().trim());

            String sql = (id == null)
                    ? "INSERT INTO cursos (nombre, horas) VALUES (?, ?)"
                    : "UPDATE cursos SET nombre = ?, horas = ? WHERE id = ?";

            try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setInt(2, horas);
                if (id != null) ps.setInt(3, id);
                ps.executeUpdate();
            }
            limpiar();
            cargar();
        } catch (NumberFormatException ex) {
            Db.aviso("Horas debe ser un número entero.");
        } catch (SQLException ex) {
            Db.aviso("Error al guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        Curso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { Db.aviso("Selecciona un curso de la tabla."); return; }
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cursos WHERE id = ?")) {
            ps.setInt(1, sel.getId());
            ps.executeUpdate();
            limpiar();
            cargar();
        } catch (SQLIntegrityConstraintViolationException ex) {
            Db.aviso("No se puede eliminar: el curso tiene matrículas asociadas.");
        } catch (SQLException ex) {
            Db.aviso("Error al eliminar: " + ex.getMessage());
        }
    }

    private void limpiar() {
        tabla.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtHoras.setText("0");
    }
}
