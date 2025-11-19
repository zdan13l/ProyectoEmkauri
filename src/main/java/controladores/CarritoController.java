package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Producto;
import modelo.Usuario;

// Controlador para gestionar el carrito de compras del cliente.
public class CarritoController {

    // Elementos de la interfaz gráfica.
    @FXML private TableView<Producto> tablaCarrito;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colEmprendedor;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private Label lblTotal;
    @FXML private Button btnPagar;
    @FXML private Button btnEliminar;
    @FXML private Button btnVaciar;
    @FXML private Button btnVolver;

    // Servicios para manejar la lógica.
    private final GestorPantallas gestorPantallas;

    // Lista observable que contiene los productos del carrito.
    private final ObservableList<Producto> productosCarrito = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public CarritoController(GestorPantallas gestorPantallas) {
        this.gestorPantallas = gestorPantallas;
    }

    // Inicializa la tabla y carga los productos del carrito.
    @FXML
    public void initialize() {
        configurarColumnas();
        cargarProductosCarrito();
        actualizarTotal();
    }

    // Configura las columnas de la tabla de productos.
    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Emprendedor (nombre).
        colEmprendedor.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> {
                    Usuario emprendedor = cellData.getValue().getEmprendedor();
                    if (emprendedor != null) {
                        return emprendedor.getDatosPersonales().getNombre() + " " + emprendedor.getDatosPersonales().getApellido();
                    } else {
                        return "DESCONOCIDO";
                    }
                })
        );

        // Tipo de producto (Curso o Servicio).
        colTipo.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> {
                    String tipo = cellData.getValue().getClass().getSimpleName();
                    if (tipo.equalsIgnoreCase("Curso")) return "CURSO";
                    if (tipo.equalsIgnoreCase("Servicio")) return "SERVICIO";
                    return "DESCONOCIDO";
                })
        );
    }

    // Carga los productos del carrito actual en la tabla.
    private void cargarProductosCarrito() {
        productosCarrito.setAll(SesionActual.getCarrito());
        tablaCarrito.setItems(productosCarrito);
    }

    // Elimina el producto seleccionado del carrito.
    @FXML
    public void handleEliminar(ActionEvent actionEvent) {
        Producto seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Confirmar eliminación.
            boolean respuesta = gestorPantallas.mostrarConfirmacion("Eliminar producto", "¿Deseas eliminar el producto seleccionado del carrito?");
            if (!respuesta) { return; }

            SesionActual.getCarrito().remove(seleccionado);
            productosCarrito.remove(seleccionado);
            actualizarTotal();
        } else {
            gestorPantallas.mostrarError("Error", "Selecciona un producto para eliminar.");
        }
    }

    // Quita todos los productos del carrito.
    @FXML
    public void handleVaciar(ActionEvent actionEvent) {
        if (productosCarrito.isEmpty()) {
            gestorPantallas.mostrarAlerta("Aviso", "El carrito ya está vacío.");
            return;
        }

        // Confirmar vaciado del carrito.
        boolean respuesta = gestorPantallas.mostrarConfirmacion("Vaciar carrito", "¿Deseas vaciar el carrito?");
        if (respuesta) {
            SesionActual.vaciarCarrito();
            productosCarrito.clear();
            actualizarTotal();
        }
    }

    // Procesa el pago y abre la pantalla de confirmación.
    @FXML
    public void handlePagar(ActionEvent actionEvent) {
        if (productosCarrito.isEmpty()) {
            gestorPantallas.mostrarAlerta("Carrito vacío", "No hay productos para procesar la compra.");
            return;
        }
        // Navega a la pantalla de pago.
        gestorPantallas.irPago();
    }

    // Vuelve a la pantalla principal del cliente.
    @FXML
    public void handleVolver(ActionEvent actionEvent) { gestorPantallas.irCliente(); }

    // Actualiza la etiqueta del total del carrito.
    private void actualizarTotal() { lblTotal.setText(String.format("$%.2f", calcularTotal())); }

    // Calcula el total sumando los precios de los productos en el carrito.
    private double calcularTotal() { return productosCarrito.stream().mapToDouble(Producto::getPrecio).sum(); }
}
