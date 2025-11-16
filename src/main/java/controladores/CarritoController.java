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
import modelo.Producto;
import modelo.Usuario;
import servicio.*;

import java.io.IOException;

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

    // Servicios para manejar la lógica.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Lista observable que contiene los productos del carrito.
    private final ObservableList<Producto> productosCarrito = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public CarritoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
                                ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal, GestorPantallas gestorPantallas) {
        this.servicioCo = servicioCo;
        this.servicioU = servicioU;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
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

        // Emprendedor (correo o nombre)
        colEmprendedor.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> {
                    Usuario emp = cellData.getValue().getEmprendedor();
                    if (emp == null) return "";
                    return emp.getCorreo() != null ? emp.getCorreo() :
                            (emp.getDatosPersonales().getNombre() != null ? emp.getDatosPersonales().getNombre() : "");
                })
        );

        // Tipo de producto (Curso o Servicio)
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

    // Agrega un producto al carrito y actualiza el total.
    public void agregarProducto(Producto producto) {
        productosCarrito.add(producto);
        actualizarTotal();
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
            gestorPantallas.mostrarAlerta("Error", "Selecciona un producto para eliminar.");
        }
    }

    // Vacia todo el carrito despues de una confirmacion.
    @FXML
    public void handleVaciar(ActionEvent actionEvent) {
        if (productosCarrito.isEmpty()) {
            gestorPantallas.mostrarAlerta("Aviso", "El carrito ya está vacío.");
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

    // Procesa el pago y abre la pantalla de confirmación.
    @FXML
    public void handlePagar(ActionEvent actionEvent) {
        if (productosCarrito.isEmpty()) {
            gestorPantallas.mostrarAlerta("Carrito vacío", "No hay productos para procesar la compra.");
            return;
        }

        gestorPantallas.irPago();
    }

    // Vuelve a la pantalla principal del cliente.
    @FXML
    public void handleVolver(ActionEvent actionEvent) {
        gestorPantallas.irCliente();
    }

    // Actualiza la etiqueta del total del carrito.
    private void actualizarTotal() {
        lblTotal.setText(String.format("$%.2f", calcularTotal()));
    }

    // Calcula el total sumando los precios de los productos en el carrito.
    private double calcularTotal() {
        return productosCarrito.stream().mapToDouble(Producto::getPrecio).sum();
    }

}
