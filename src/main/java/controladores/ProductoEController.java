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

// Controlador para gestionar los productos de un emprendedor
public class ProductoEController {

    // Elementos de la interfaz gráfica.
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colTitulo;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colEstado;
    @FXML private Button btnVolver;
    @FXML private Button btnAdministrar;

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Lista observable para los productos del emprendedor.
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public ProductoEController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
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
        cargarProductosAsync();
    }

    // Configura las columnas de la tabla de productos.
    private void configurarTabla() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        colCategoria.setCellValueFactory(data -> {
            if (data.getValue().getCategoria() != null)
                return new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria().getNombre());
            return new javafx.beans.property.SimpleStringProperty("Sin categoría");
        });

        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        colTipo.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getClass().getSimpleName()));

        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Activo"));

        tablaProductos.setItems(productos);
    }

    // Carga los productos del emprendedor de forma asíncrona.
    private void cargarProductosAsync() {
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            gestorPantallas.mostrarAlerta("Error de sesión", "No hay usuario autenticado.");
            return;
        }

        int idEmprendedor = actual.getIdUsuario();

        Task<List<Producto>> task = new Task<>() {
            @Override
            protected List<Producto> call() {
                return servicioP.listarPorEmprendedor(idEmprendedor);
            }
        };

        task.setOnSucceeded(e -> productos.setAll(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            gestorPantallas.mostrarAlerta("Error", "No se pudieron cargar los productos: " + (ex == null ? "error desconocido" : ex.getMessage()));
            ex.printStackTrace();
        });

        new Thread(task).start();
    }

    // Maneja la acción de administrar un producto seleccionado.
    @FXML
    private void handleAdministrar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            gestorPantallas.mostrarAlerta("Seleccione un producto", "Debe seleccionar un producto para administrarlo.");
            return;
        }
        abrirPantallaAdministrar(seleccionado);
    }

    // Abre la pantalla para administrar el producto seleccionado.
    private void abrirPantallaAdministrar(Producto producto) {
        gestorPantallas.irAdminCurso();
    }

    // Maneja la acción de volver al menú del emprendedor.
    @FXML
    private void handleVolver() {
        gestorPantallas.irEmprendedor();
    }
}
