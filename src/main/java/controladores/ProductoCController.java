package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Curso;
import modelo.Producto;
import modelo.Servicio;
import modelo.Usuario;
import servicio.*;
import java.util.List;

// Controlador para gestionar la vista de productos comprados por el cliente.
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

    // Servicios para la lógica y gestor de navegación.
    private final ISProducto servicioP;
    private final GestorPantallas gestorPantallas;

    // Lista observable para los productos del cliente.
    private final ObservableList<Producto> productosCliente = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public ProductoCController(ISProducto servicioP, GestorPantallas gestorPantallas) {
        this.servicioP = servicioP;
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
                return new SimpleStringProperty(data.getValue().getCategoria().getNombre());
            return new SimpleStringProperty("Sin categoría");
        });

        colEmprendedor.setCellValueFactory(data -> {
            if (data.getValue().getEmprendedor() != null)
                return new SimpleStringProperty(
                        data.getValue().getEmprendedor().getDatosPersonales().getNombre() + " " +
                        data.getValue().getEmprendedor().getDatosPersonales().getApellido());
            return new SimpleStringProperty("Desconocido");
        });

        tablaProductosCliente.setItems(productosCliente);
    }

    // Carga los productos comprados por el cliente de forma asíncrona.
    private void cargarProductosCompradosAsync() {
        Usuario actual = SesionActual.getUsuarioActual();
        if (actual == null) {
            gestorPantallas.mostrarError("Error de sesión", "No hay un usuario autenticado en la sesión.");
            return;
        }
        // Obtener el ID del cliente actual.
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
            gestorPantallas.mostrarError("Error", "No se pudieron cargar los productos comprados: " + (ex == null ? "error desconocido" : ex.getMessage()));
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

        // Si el producto es un curso -> abrir la pantalla específica del curso
        if (seleccionado instanceof Curso) {
            gestorPantallas.irAccederCurso(seleccionado);
        } else if (seleccionado instanceof Servicio) {
            gestorPantallas.irAccederServicio(seleccionado);
        } else {
            gestorPantallas.mostrarAlerta("Tipo de producto desconocido", "El tipo de producto seleccionado no es reconocido.");
        }
    }

    // Maneja la acción de calificar los productos.
    @FXML
    private void onCalificaciones() {
        Producto seleccionado = tablaProductosCliente.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            gestorPantallas.mostrarAlerta("Seleccione un producto", "Debe seleccionar un producto para calificarlo.");
            return;
        }
        gestorPantallas.irCalificar(seleccionado);
    }

    // Cambia a la pantalla del menú del cliente.
    @FXML
    private void onVolver() { gestorPantallas.irCliente(); }
}
