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

/**
 * Controlador de la vista ProductosC.fxml
 * Muestra los productos adquiridos por el cliente (comprador).
 */
public class ProductoCController {

    @FXML private TableView<Producto> tablaProductosCliente;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, String> colEmprendedor;

    @FXML private Button btnVerDetalle;
    @FXML private Button btnCalificaciones;
    @FXML private Button btnVolver;
    @FXML private Button btnVolverHeader;

    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    private final ObservableList<Producto> productosCliente = FXCollections.observableArrayList();

    // -------------------------------------------------------------------------
    // CONSTRUCTOR CON INYECCIÓN DE DEPENDENCIAS
    // -------------------------------------------------------------------------
    public ProductoCController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    // -------------------------------------------------------------------------
    // INICIALIZACIÓN
    // -------------------------------------------------------------------------
    @FXML
    private void initialize() {
        configurarTabla();
        cargarProductosCompradosAsync();
    }

    // -------------------------------------------------------------------------
    // CONFIGURACIÓN TABLA
    // -------------------------------------------------------------------------
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colCategoria.setCellValueFactory(data -> {
            if (data.getValue().getCategoria() != null)
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria().getNombre());
            else
                return new javafx.beans.property.SimpleStringProperty("Sin categoría");
        });

        colEmprendedor.setCellValueFactory(data -> {
            if (data.getValue().getEmprendedor() != null)
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getEmprendedor().getDatosPersonales().getNombre());
            else
                return new javafx.beans.property.SimpleStringProperty("Desconocido");
        });

        tablaProductosCliente.setItems(productosCliente);
    }

    // -------------------------------------------------------------------------
    // CARGA DE PRODUCTOS COMPRADOS (ASÍNCRONA)
    // -------------------------------------------------------------------------
    private void cargarProductosCompradosAsync() {
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de sesión", null,
                    "No hay un usuario autenticado en la sesión.");
            return;
        }

        int idCliente = actual.getIdUsuario();

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                // Método que debes tener en ISProducto/SProducto/RProducto:
                // listarCompradosPorCliente(int idCliente)
                return servicioP.listarComprados(idCliente);
            }
        };

        task.setOnSucceeded(e -> {
            List<Producto> lista = task.getValue();
            System.out.println("Productos comprados cargados para cliente " + idCliente + ": " +
                    (lista == null ? 0 : lista.size()));
            productosCliente.setAll(lista);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudieron cargar los productos comprados: " +
                            (ex == null ? "error desconocido" : ex.getMessage()));
            ex.printStackTrace();
        });

        new Thread(task).start();
    }

    // -------------------------------------------------------------------------
    // ACCIONES DE BOTONES
    // -------------------------------------------------------------------------

    @FXML
    private void onVerDetalle() {
        Producto seleccionado = tablaProductosCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un producto", null,
                    "Debe seleccionar un producto para ver su detalle.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/detalleProducto.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnVerDetalle.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Detalle de " + seleccionado.getTitulo());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudo abrir la pantalla de detalle del producto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCalificaciones() {
        Producto seleccionado = tablaProductosCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un producto", null,
                    "Debe seleccionar un producto para ver sus calificaciones.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/calificaciones.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnCalificaciones.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Calificaciones de " + seleccionado.getTitulo());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudo abrir la pantalla de calificaciones.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onVolver() {
        irAMenuCliente();
    }

    @FXML
    private void onVolverHeader() {
        irAMenuCliente();
    }

    private void irAMenuCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/cliente.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Menú del Cliente");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudo regresar al menú del cliente.");
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // MÉTODO AUXILIAR
    // -------------------------------------------------------------------------
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
