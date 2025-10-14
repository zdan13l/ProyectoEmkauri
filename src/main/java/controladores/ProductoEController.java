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

public class ProductoEController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colTitulo;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colEstado;

    @FXML private Button btnVolver;
    @FXML private Button btnAdministrar;

    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    public ProductoEController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP,
                               ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    @FXML
    private void initialize() {
        configurarTabla();
        cargarProductosAsync();
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoria.setCellValueFactory(data -> {
            if (data.getValue().getCategoria() != null)
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria().getNombre());
            else
                return new javafx.beans.property.SimpleStringProperty("Sin categoría");
        });
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colTipo.setCellValueFactory(data -> {
            String tipo = data.getValue().getClass().getSimpleName();
            return new javafx.beans.property.SimpleStringProperty(tipo);
        });

        // Si quieres mostrar estado real (por ejemplo a partir de Solicitudes),
        // reemplaza el "Activo" por la lógica necesaria. Por ahora lo dejamos fijo.
        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty("Activo")
        );

        tablaProductos.setItems(productos);
    }

    private void cargarProductosAsync() {
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de sesión", null, "No hay usuario autenticado.");
            return;
        }

        int idEmprendedor = actual.getIdUsuario();

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                // Llamamos al nuevo método del servicio que devuelve TODOS los productos del emprendedor.
                // Asegúrate de implementar listarPorEmprendedor en ISProducto/SProducto/RProducto.
                return servicioP.listarPorEmprendedor(idEmprendedor);
            }
        };

        task.setOnSucceeded(e -> {
            List<Producto> lista = task.getValue();
            System.out.println("Productos cargados para emprendedor " + idEmprendedor + ": " + (lista == null ? 0 : lista.size()));
            productos.setAll(lista);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudieron cargar los productos: " + (ex == null ? "error desconocido" : ex.getMessage()));
            ex.printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void handleAdministrar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un producto", null,
                    "Debe seleccionar un producto de la tabla para administrarlo.");
            return;
        }

        abrirPantallaAdministrar(seleccionado);
    }

    private void abrirPantallaAdministrar(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/administrarProducto.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Administrar " + producto.getTitulo());
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudo abrir la pantalla de administración del producto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/emprendedor.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Menú del Emprendedor");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                    "No se pudo regresar al menú del emprendedor.");
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
