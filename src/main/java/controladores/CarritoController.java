package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.binding.Bindings;
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
import servicio.ISCategoria;
import servicio.ISCompra;
import servicio.ISProducto;
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

    // Servicios para manejar la lógica de usuario, compra, producto y categoría.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;

    private final ObservableList<Producto> productosCarrito = FXCollections.observableArrayList();

    public CarritoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
    }

    // Inicializa la tabla y carga los productos del carrito.
    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla.
        colNombre.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Emprendedor (correo o nombre).
        colEmprendedor.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> {
                    Usuario emp = cellData.getValue().getEmprendedor();
                    if (emp == null) return "";
                    // Mostrar nombre si existe, de lo contrario el correo
                    return emp.getCorreo() != null ? emp.getCorreo() :
                            (emp.getDatosPersonales().getNombre() != null ? emp.getDatosPersonales().getNombre() : "");
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

        // Cargar los productos del carrito actual.
        productosCarrito.setAll(SesionActual.getCarrito());
        tablaCarrito.setItems(productosCarrito);
        actualizarTotal();
    }

    // Agrega un producto al carrito y actualiza el total.
    public void agregarProducto(Producto producto) {
        productosCarrito.add(producto);
        actualizarTotal();
    }

    // Procesa el pago y crea la compra.
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

            Pago pago = new Pago();
            pago.setMonto(total);
            pago.setMetodo("Tarjeta");
            pago.setFecha(new Date());

            Compra compra = new Compra();
            compra.setCliente(usuarioActual);
            compra.setProductos(new ArrayList<>(productosCarrito));
            compra.setMontoFinal(total);
            compra.setFechaCompra(new Date());
            compra.setPago(pago);

            boolean exito = servicioCo.crearCompra(compra);

            if (exito) {
                mostrarAlerta("Compra realizada", "Tu compra fue registrada con éxito.");
                productosCarrito.clear();
                SesionActual.vaciarCarrito();
                actualizarTotal();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la compra.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al procesar la compra: " + e.getMessage());
        }
    }

    // Vuelve a la pantalla principal del cliente.
    @FXML
    public void handleVolver(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/cliente.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setTitle("Menú del Cliente");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver a la pantalla del cliente.");
        }
    }

    // Vacía todo el carrito después de una confirmación.
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
                SesionActual.vaciarCarrito();
                productosCarrito.clear();
                actualizarTotal();
            }
        });
    }

    // Elimina el producto seleccionado del carrito.
    @FXML
    public void handleEliminar(ActionEvent actionEvent) {
        Producto seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            SesionActual.getCarrito().remove(seleccionado);
            productosCarrito.remove(seleccionado);
            actualizarTotal();
        } else {
            mostrarAlerta("Error", "Selecciona un producto para eliminar.");
        }
    }

    // Actualiza la etiqueta del total con el monto calculado.
    private void actualizarTotal() {
        lblTotal.setText(String.format("$%.2f", calcularTotal()));
    }

    // Calcula el total sumando los precios de los productos en el carrito.
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
