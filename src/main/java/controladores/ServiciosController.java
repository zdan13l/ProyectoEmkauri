package controladores;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ServiciosController {

    // Tabla
    @FXML private TableView<OfertaItem> tablaOfertas;
    @FXML private TableColumn<OfertaItem, String> colNombre;
    @FXML private TableColumn<OfertaItem, String> colTipo;
    @FXML private TableColumn<OfertaItem, String> colCategoria;
    @FXML private TableColumn<OfertaItem, String> colPrecio;

    // Filtros / búsqueda
    @FXML private TextField txtBuscar;

    // Detalle
    @FXML private Label lblNombre, lblTipo, lblCategoria, lblPrecio;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnSolicitar;

    private final ObservableList<OfertaItem> ofertas = FXCollections.observableArrayList();
    private final ObservableList<OfertaItem> filtradas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mapeo columnas
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria()));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrecioFormateado()));

        // Datos mock para UI
        ofertas.addAll(
                new OfertaItem("Curso de JavaFX", "Curso", "Programación", 120000,
                        "Aprende a crear interfaces con JavaFX desde cero."),
                new OfertaItem("Mentoría UX 1:1", "Servicio", "Diseño", 90000,
                        "Sesión personalizada para revisar tu portafolio UX."),
                new OfertaItem("Curso SQL Básico", "Curso", "Bases de datos", 80000,
                        "Fundamentos de SQL: SELECT, JOIN, funciones y vistas."),
                new OfertaItem("Edición de Video", "Servicio", "Audiovisual", 150000,
                        "Edición de video profesional para redes sociales.")
        );
        filtradas.setAll(ofertas);

        tablaOfertas.setItems(filtradas);

        // Selección → muestra detalle
        tablaOfertas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) mostrarDetalle(sel);
        });

        // Búsqueda simple
        txtBuscar.textProperty().addListener((obs, old, q) -> filtrar(q));

        // Estado inicial
        if (!filtradas.isEmpty()) {
            tablaOfertas.getSelectionModel().selectFirst();
        }
    }

    private void mostrarDetalle(OfertaItem o) {
        lblNombre.setText(o.getNombre());
        lblTipo.setText(o.getTipo());
        lblCategoria.setText(o.getCategoria());
        lblPrecio.setText(o.getPrecioFormateado());
        txtDescripcion.setText(o.getDescripcion());
    }

    private void filtrar(String query) {
        if (query == null || query.isBlank()) {
            filtradas.setAll(ofertas);
            return;
        }
        String q = query.toLowerCase();
        filtradas.setAll(ofertas.filtered(o ->
                o.getNombre().toLowerCase().contains(q) ||
                o.getCategoria().toLowerCase().contains(q) ||
                o.getTipo().toLowerCase().contains(q)
        ));
        if (!filtradas.isEmpty()) tablaOfertas.getSelectionModel().selectFirst();
        else {
            lblNombre.setText(""); lblTipo.setText(""); lblCategoria.setText(""); lblPrecio.setText("");
            txtDescripcion.setText("");
        }
    }

    // Botones (UI-only)
    @FXML
    private void handleVerMas() {
        new Alert(Alert.AlertType.INFORMATION,
                "Aquí iría una vista de detalle ampliado de la oferta (UI).").showAndWait();
    }

    @FXML
    private void handleSolicitar() {
        new Alert(Alert.AlertType.INFORMATION,
                "Acción de 'Solicitar' pendiente de lógica/validación (no incluida en esta tarea).").showAndWait();
    }

    // ===== DTO UI =====
    public static class OfertaItem {
        private final String nombre, tipo, categoria, descripcion;
        private final int precio; // COP

        public OfertaItem(String nombre, String tipo, String categoria, int precio, String descripcion) {
            this.nombre = nombre; this.tipo = tipo; this.categoria = categoria;
            this.precio = precio; this.descripcion = descripcion;
        }
        public String getNombre() { return nombre; }
        public String getTipo() { return tipo; }
        public String getCategoria() { return categoria; }
        public String getDescripcion() { return descripcion; }
        public String getPrecioFormateado() { return String.format("$%,d", precio).replace(',', '.'); }
    }
}
