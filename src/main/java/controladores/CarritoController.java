package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.Compra;
import modelo.Pago;
import modelo.Producto;
import modelo.Usuario;
import servicio.ISCompra;
import servicio.ISUsuario;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

// Controlador para manejar la lógica de la pantalla del carrito de compras.
public class CarritoController {

    // Campos vinculados a los elementos de la interfaz.
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

    // Servicios para manejar la lógica de usuario y compras.
    private final ISUsuario servicioUsuario;
    private final ISCompra servicioCompra;

    private final ObservableList<Producto> productosCarrito = FXCollections.observableArrayList();

    public CarritoController(ISUsuario servicioUsuario, ISCompra servicioCompra) {
        this.servicioUsuario = servicioUsuario;
        this.servicioCompra = servicioCompra;
    }

    // Inicialización del controlador.
    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colEmprendedor.setCellValueFactory(new PropertyValueFactory<>("nombreEmprendedor"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        tablaCarrito.setItems(productosCarrito);
        actualizarTotal();
    }

    // Método para agregar un producto al carrito
    public void agregarProducto(Producto producto) {
        productosCarrito.add(producto);
        actualizarTotal();
    }

    // PAGAR - Procesa la compra de los productos en el carrito.
    @FXML
    public void handlePagar(ActionEvent actionEvent) {
        if (productosCarrito.isEmpty()) {
            mostrarAlerta("Carrito vacío", "No hay productos para procesar la compra.");
            return;
        }

        Usuario usuarioActual = SesionActual.getUsuarioActual();
        if (usuarioActual == null) {
            mostrarAlerta("Error", "No hay usuario autenticado.");
            return;
        }

        try {
            double total = calcularTotal();

            // Crear el objeto de pago.
            Pago pago = new Pago();
            pago.setMonto(total);
            pago.setMetodo("Tarjeta");
            pago.setFecha(new Date());

            // Crear la compra.
            Compra compra = new Compra();
            compra.setCliente(usuarioActual);
            compra.setProductos(new ArrayList<>(productosCarrito));
            compra.setMontoFinal(total);
            compra.setFechaCompra(new Date());
            compra.setPago(pago);

            // Guardar la compra usando el servicio.
            boolean exito = servicioCompra.crearCompra(compra);

            if (exito) {
                mostrarAlerta("Compra realizada", "Tu compra fue registrada con éxito.");
                productosCarrito.clear();
                actualizarTotal();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la compra.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al procesar la compra: " + e.getMessage());
        }
    }

    // VOLVER - Regresa a la pantalla del cliente.
    @FXML
    public void handleVolver(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/cliente.fxml"));
            Controlador controladorFactory = new Controlador(servicioUsuario, servicioCompra);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setTitle("Menú del Cliente");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver a la pantalla del cliente.");
        }
    }

    // VACIAR - Limpia completamente el carrito.
    @FXML
    public void handleVaciar(ActionEvent actionEvent) {
        if (productosCarrito.isEmpty()) {
            mostrarAlerta("Aviso", "El carrito ya está vacío.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas vaciar el carrito?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmación");
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                productosCarrito.clear();
                actualizarTotal();
            }
        });
    }

    // ELIMINAR - Borra solo el producto seleccionado.
    @FXML
    public void handleEliminar(ActionEvent actionEvent) {
        Producto seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            productosCarrito.remove(seleccionado);
            actualizarTotal();
        } else {
            mostrarAlerta("Error", "Selecciona un producto para eliminar.");
        }
    }

    // Actualiza la etiqueta del total con el monto actual del carrito.
    private void actualizarTotal() {
        lblTotal.setText(String.format("$%.2f", calcularTotal()));
    }

    // Calcula el total sumando los precios de todos los productos en el carrito.
    private double calcularTotal() {
        return productosCarrito.stream().mapToDouble(Producto::getPrecio).sum();
    }

    // Muestra una alerta con el título y mensaje proporcionados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
