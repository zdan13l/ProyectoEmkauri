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

    // Lista observable para los productos del cliente.
    private final ObservableList<Producto> productosCliente = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public ProductoCController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
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
            mostrarAlerta("Error de sesión", "No hay un usuario autenticado en la sesión.");
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
            mostrarAlerta("Error", "No se pudieron cargar los productos comprados: " + (ex == null ? "error desconocido" : ex.getMessage()));
        });

        new Thread(task).start();
    }

    // Maneja la acción de ver el detalle del producto seleccionado.
    @FXML
    private void onVerDetalle() {
        Producto seleccionado = tablaProductosCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Seleccione un producto", "Debe seleccionar un producto para ver su detalle.");
            return;
        }

        cambiarPantalla("/puj.fis.pantallas/detalleProducto.fxml", "Detalle de " + seleccionado.getTitulo(), btnVerDetalle);
    }

    // Maneja la acción de calificar los productos.
    @FXML
    private void onCalificaciones() {
        cambiarPantalla("/puj.fis.pantallas/calificar.fxml", "Calificaciones de Productos", btnCalificaciones);
    }

    // Maneja la acción de volver al menú del cliente.
    @FXML
    private void onVolver() {
        irAMenuCliente();
    }

    // Maneja la acción de volver al menú del cliente desde el header.
    @FXML
    private void onVolverHeader() {
        cambiarPantalla("/puj.fis.pantallas/cliente.fxml", "Menú del Cliente", btnVolverHeader);
    }

    // Cambia a la pantalla del menú del cliente.
    private void irAMenuCliente() {
        cambiarPantalla("/puj.fis.pantallas/cliente.fxml", "Menú del Cliente", btnVolver);
    }

    // Muestra una alerta con el tipo, título, encabezado y mensaje especificados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Cambia a otra pantalla especificada por el path del FXML, título y botón que origina el cambio.
    private void cambiarPantalla(String fxmlPath, String titulo, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS, servicioCal);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla: " + titulo);
        }
    }
}
