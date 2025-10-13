package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Compra;
import modelo.Producto;
import modelo.Usuario;
import modelo.Pago;
import servicio.ISCompra;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CarritoController {

    // --------------------------
    // 🔹 Referencias FXML
    // --------------------------
    @FXML private TableView<Producto> tablaCarrito;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private Label lblTotal;
    @FXML private Button btnComprar;
    @FXML private Button btnEliminar;

    // --------------------------
    // 🔹 Dependencias inyectadas
    // --------------------------
    private final ISCompra servicioCompra;
    private final Usuario usuarioLogueado;

    // --------------------------
    // 🔹 Estado interno del carrito
    // --------------------------
    private final ObservableList<Producto> productosCarrito = FXCollections.observableArrayList();

    // --------------------------
    // 🔹 Constructor con dependencias
    // --------------------------
    public CarritoController(ISCompra servicioCompra, Usuario usuarioLogueado) {
        this.servicioCompra = servicioCompra;
        this.usuarioLogueado = usuarioLogueado;
    }

    // --------------------------
    // 🔹 Inicialización de la vista
    // --------------------------
    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Asociar la lista observable con la tabla
        tablaCarrito.setItems(productosCarrito);

        // Mostrar el total inicial
        actualizarTotal();
    }

    // --------------------------
    // 🔹 Métodos del controlador
    // --------------------------

    public void agregarProducto(Producto producto) {
        productosCarrito.add(producto);
        actualizarTotal();
    }

    @FXML
    public void eliminarProducto(ActionEvent event) {
        Producto seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            productosCarrito.remove(seleccionado);
            actualizarTotal();
        } else {
            mostrarAlerta("Error", "Selecciona un producto para eliminar.");
        }
    }

    @FXML
    public void realizarCompra(ActionEvent event) {
        if (productosCarrito.isEmpty()) {
            mostrarAlerta("Error", "El carrito está vacío.");
            return;
        }

        try {
            // Crear objeto compra
            Compra compra = new Compra();
            compra.setCliente(usuarioLogueado);
            compra.setProductos(new ArrayList<>(productosCarrito));
            compra.setMontoFinal(calcularTotal());
            compra.setFechaCompra(java.sql.Date.valueOf(LocalDate.now()));

            // Simular un pago (podrías cambiarlo si tienes pantalla de pago)
            Pago pago = new Pago();
            pago.setIdPago(1); // ejemplo
            compra.setPago(pago);

            // Guardar compra
            boolean exito = servicioCompra.crearCompra(compra);

            if (exito) {
                mostrarAlerta("Compra exitosa", "Tu compra se ha realizado correctamente.");
                productosCarrito.clear();
                actualizarTotal();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la compra.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un problema al procesar la compra: " + e.getMessage());
        }
    }

    // --------------------------
    // 🔹 Métodos auxiliares
    // --------------------------

    private void actualizarTotal() {
        lblTotal.setText(String.format("Total: $%.2f", calcularTotal()));
    }

    private double calcularTotal() {
        return productosCarrito.stream().mapToDouble(Producto::getPrecio).sum();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
