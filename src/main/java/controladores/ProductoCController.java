package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.util.List;

// Controlador para mostrar los productos adquiridos por el cliente
public class ProductoCController {

    // Elementos de la interfaz gráfica.
    @FXML private TableView<Producto> tablaProductosCliente;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, String> colEmprendedor;
    @FXML private Button btnVerDetalle;
    @FXML private Button btnCalificaciones;
    @FXML private Button btnVolver;
    @FXML private Button btnVolverHeader;

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Lista observable para los productos del cliente.
    private final ObservableList<Producto> productosCliente = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public ProductoCController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
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

    // Inicialización del controlador.
    @FXML
    private void initialize() {
        configurarTabla();
        cargarProductosCompradosAsync();
    }

    // Configura las columnas de la tabla.
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colCategoria.setCellValueFactory(data -> {
            if (data.getValue().getCategoria() != null)
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria().getNombre());
            return new javafx.beans.property.SimpleStringProperty("Sin categoría");
        });

        colEmprendedor.setCellValueFactory(data -> {
            if (data.getValue().getEmprendedor() != null)
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getEmprendedor().getDatosPersonales().getNombre());
            return new javafx.beans.property.SimpleStringProperty("Desconocido");
        });

        tablaProductosCliente.setItems(productosCliente);
    }

    // Carga los productos comprados por el cliente de forma asíncrona.
    private void cargarProductosCompradosAsync() {
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            gestorPantallas.mostrarAlerta("Error de sesión", "No hay un usuario autenticado en la sesión.");
            return;
        }

        int idCliente = actual.getIdUsuario();

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                return servicioP.listarComprados(idCliente);
            }
        };

        task.setOnSucceeded(e -> productosCliente.setAll(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            gestorPantallas.mostrarAlerta("Error", "No se pudieron cargar los productos comprados: " + (ex == null ? "error desconocido" : ex.getMessage()));
        });

        new Thread(task).start();
    }

    // Maneja la acción de ver el detalle del producto seleccionado.
    @FXML
    private void onVerDetalle() {
        Producto seleccionado = tablaProductosCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            gestorPantallas.mostrarAlerta("Seleccione un producto", "Debe seleccionar un producto para ver su detalle.");
            return;
        }
        // Pasar el producto seleccionado a la pantalla de detalle. (PENDIENTE)
    }

    // Maneja la acción de calificar los productos.
    @FXML
    private void onCalificaciones() {
        gestorPantallas.irCalificar();
    }

    // Maneja la acción de volver al menú del cliente.
    @FXML
    private void onVolver() {
        irAMenuCliente();
    }

    // Maneja la acción de volver al menú del cliente desde el header.
    @FXML
    private void onVolverHeader() {
        gestorPantallas.irCliente();
    }

    // Cambia a la pantalla del menú del cliente.
    private void irAMenuCliente() {
        gestorPantallas.irCliente();
    }
}
