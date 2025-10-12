package controladores;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Producto; // usamos lo que ya existe

public class CarritoController {

    @FXML private TableView<CarritoItem> tablaCarrito;
    @FXML private TableColumn<CarritoItem, String>  colNombre;
    @FXML private TableColumn<CarritoItem, String>  colCategoria;
    @FXML private TableColumn<CarritoItem, Double>  colPrecio;
    @FXML private TableColumn<CarritoItem, Integer> colCantidad;
    @FXML private TableColumn<CarritoItem, Double>  colTotal;

    @FXML private Label lblTotal;
    @FXML private Button btnPagar, btnEliminar, btnVaciar, btnVolver;

    private final ObservableList<CarritoItem> carrito = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mapea a propiedades del DTO CarritoItem (no dependemos de getters inexistentes en Producto)
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tablaCarrito.setItems(carrito);

        // --- MOCK de prueba para ver la UI funcionando (puedes borrar cuando conecten la lógica) ---
        // Si Producto tiene getTitulo() y getPrecio(), esto compila.
        // Agrega 2 filas de ejemplo para validar la vista.
        carrito.add(CarritoItem.fromProducto(mockProducto("Curso de JavaFX", 120_000)));
        carrito.add(CarritoItem.fromProducto(mockProducto("Servicio de Mentoría", 80_000)));
        actualizarTotal();
    }

    // Utilidad: actualiza total
    private void actualizarTotal() {
        double total = carrito.stream().mapToDouble(CarritoItem::getTotal).sum();
        lblTotal.setText(String.format("$%,.0f", total).replace(',', '.'));
    }

    // Handlers
    @FXML
    private void handleEliminar() {
        CarritoItem sel = tablaCarrito.getSelectionModel().getSelectedItem();
        if (sel != null) {
            carrito.remove(sel);
            actualizarTotal();
        }
    }

    @FXML
    private void handleVaciar() {
        carrito.clear();
        actualizarTotal();
    }

    @FXML
    private void handlePagar() {
        // Solo UI: redirección se hará cuando integren CU-009 (pagos)
        new Alert(Alert.AlertType.INFORMATION, "Redirigiendo a Gestión de Pagos...").showAndWait();
    }

    @FXML
    private void handleVolver() {
        // Solo UI: aquí cargarías el catálogo cuando exista la navegación
        new Alert(Alert.AlertType.INFORMATION, "Volviendo al Catálogo...").showAndWait();
    }

    // ===================== DTO de la tabla (UI only) =====================
    public static class CarritoItem {
        private final StringProperty  nombre    = new SimpleStringProperty();
        private final StringProperty  categoria = new SimpleStringProperty("—"); // placeholder
        private final DoubleProperty  precio    = new SimpleDoubleProperty(0);
        private final IntegerProperty cantidad  = new SimpleIntegerProperty(1);
        private final DoubleProperty  total     = new SimpleDoubleProperty(0);

        public static CarritoItem fromProducto(Producto p) {
            CarritoItem i = new CarritoItem();
            // Campos seguros del modelo Producto (presentes en esta rama):
            i.setNombre(p.getTitulo());
            i.setPrecio(p.getPrecio());
            i.setCantidad(1);
            i.setTotal(i.getPrecio() * i.getCantidad());

            // Si en el futuro exponen nombre de categoría, aquí se setea (i.setCategoria(...))
            return i;
        }

        // Getters/Setters para PropertyValueFactory
        public String getNombre() { return nombre.get(); }
        public void setNombre(String v) { nombre.set(v); }

        public String getCategoria() { return categoria.get(); }
        public void setCategoria(String v) { categoria.set(v); }

        public double getPrecio() { return precio.get(); }
        public void setPrecio(double v) { precio.set(v); }

        public int getCantidad() { return cantidad.get(); }
        public void setCantidad(int v) { cantidad.set(v); }

        public double getTotal() { return total.get(); }
        public void setTotal(double v) { total.set(v); }
    }

    // ===================== MOCK simple para probar la UI sin tocar servicios =====================
    private Producto mockProducto(String titulo, double precio) {
        // Crea un Producto “vacío” y setea solo lo que necesitamos para la vista
        Producto p = new Producto();
        p.setTitulo(titulo);
        p.setPrecio(precio);
        return p;
    }
}
