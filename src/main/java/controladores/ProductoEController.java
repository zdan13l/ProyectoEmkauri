package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.*;
import servicio.*;
import java.util.List;

// Controlador para gestionar la vista de productos del emprendedor.
public class ProductoEController {

    // Elementos de la interfaz gráfica.
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colTitulo;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colEstado;
    @FXML private Button btnCalificaciones;
    @FXML private Button btnVolver;
    @FXML private Button btnAdministrar;

    // Servicios para la lógica y gestor de navegación.
    private final ISProducto servicioP;
    private final GestorPantallas gestorPantallas;

    // Lista observable para los productos del emprendedor.
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public ProductoEController(ISProducto servicioP, GestorPantallas gestorPantallas) {
        this.servicioP = servicioP;
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
                return new SimpleStringProperty(data.getValue().getCategoria().getNombre());
            return new SimpleStringProperty("Sin categoría");
        });

        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClass().getSimpleName()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        tablaProductos.setItems(productos);
    }

    // Carga los productos del emprendedor de forma asíncrona.
    private void cargarProductosAsync() {
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            gestorPantallas.mostrarError("Error de sesión", "No hay usuario autenticado.");
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
            gestorPantallas.mostrarError("Error", "No se pudieron cargar los productos.");
        });

        new Thread(task).start();
    }

    // Maneja la acción de administrar un producto seleccionado.
    @FXML
    private void handleAdministrar() {
        // Abrir la pantalla de administración del producto seleccionado.
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            gestorPantallas.mostrarAlerta("Seleccione un producto", "Debe seleccionar un producto para administrarlo.");
            return;
        }

        // Determinar la pantalla adecuada según el tipo de producto.
        if (seleccionado instanceof  Curso) {
            gestorPantallas.irAdminCurso(seleccionado);
        } else if (seleccionado instanceof Servicio) {
            gestorPantallas.irAdminServicio(seleccionado);
        } else {
            gestorPantallas.mostrarError("Error", "Tipo de producto no soportado para administración.");
        }
    }

    // Maneja la acción de ir a la pantalla de calificaciones.
    @FXML
    private void onCalificaciones() { gestorPantallas.irCalificaciones(); }

    // Maneja la acción de volver al menú del emprendedor.
    @FXML
    private void onVolver() { gestorPantallas.irEmprendedor(); }
}
