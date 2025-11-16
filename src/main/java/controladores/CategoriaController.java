package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Categoria;
import servicio.*;
import java.util.List;

// Controlador para gestionar las categorías de productos.
public class CategoriaController {

    // Elementos de la interfaz gráfica.
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colDescripcion;
    @FXML private Button btnVolver;

    // Servicios para la lógica y gestor de navegación.
    private final ISCategoria servicioCa;
    private final GestorPantallas gestorPantallas;

    // Lista observable de categorías para la tabla.
    private final ObservableList<Categoria> listaObservable = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public CategoriaController(ISCategoria servicioCa, GestorPantallas gestorPantallas) {
        this.servicioCa = servicioCa;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicializa la tabla y carga las categorías.
    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tablaCategorias.setItems(listaObservable);
        cargarListaAsync();

        tablaCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nueva) -> {
            if (nueva != null) {
                txtNombre.setText(nueva.getNombre());
                txtDescripcion.setText(nueva.getDescripcion());
            }
        });
        btnVolver.disableProperty().set(false);
    }

    // Agrega una nueva categoría.
    @FXML
    private void handleAgregar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            gestorPantallas.mostrarError("Validación", "El nombre no puede estar vacío.");
            return;
        }

        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        if (descripcion.isEmpty()) {
            nueva.setDescripcion("Sin descripción");
        } else {
            nueva.setDescripcion(descripcion);
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return servicioCa.crearCategoria(nueva);
            }
        };

        task.setOnSucceeded(evt -> {
            if (task.getValue()) {
                gestorPantallas.mostrarExito("Éxito", "Categoría creada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                gestorPantallas.mostrarError("Error", "No se pudo crear la categoría.");
            }
        });

        task.setOnFailed(evt -> gestorPantallas.mostrarError("Error", "Error al crear la categoría: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // Actualiza la categoría seleccionada.
    @FXML
    private void handleActualizar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            gestorPantallas.mostrarAlerta("Atención", "Seleccione una categoría para actualizar.");
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevaDescripcion = txtDescripcion.getText().trim();

        if (nuevoNombre.isEmpty()) {
            gestorPantallas.mostrarAlerta("Validación", "El nombre no puede estar vacío.");
            return;
        }

        Categoria actualizada = new Categoria();
        actualizada.setIdCategoria(seleccion.getIdCategoria());
        actualizada.setNombre(nuevoNombre);
        actualizada.setDescripcion(nuevaDescripcion.isEmpty() ? "Sin descripción" : nuevaDescripcion);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return servicioCa.actualizarCategoria(actualizada);
            }
        };

        task.setOnSucceeded(evt -> {
            if (task.getValue()) {
                gestorPantallas.mostrarExito("Éxito", "Categoría actualizada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                gestorPantallas.mostrarAlerta("Error", "No se pudo actualizar la categoría.");
            }
        });

        task.setOnFailed(evt -> gestorPantallas.mostrarAlerta("Error", "Error al actualizar: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // Elimina la categoría seleccionada.
    @FXML
    private void handleEliminar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            gestorPantallas.mostrarAlerta("Atención", "Seleccione una categoría para eliminar.");
            return;
        }
        // Usar confirmación del gestor de pantallas
        boolean confirmado = gestorPantallas.mostrarConfirmacion("Confirmar eliminación", "¿Eliminar la categoría '" + seleccion.getNombre() + "'?");

        if (!confirmado) { return; }
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() { return servicioCa.eliminarCategoria(seleccion.getIdCategoria()); }
        };

        task.setOnSucceeded(evt -> {
            if (task.getValue()) {
                gestorPantallas.mostrarExito("Éxito", "Categoría eliminada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                gestorPantallas.mostrarAlerta("Error", "No se pudo eliminar la categoría, verifique que no tenga productos asociados.");
            }
        });
        new Thread(task).start();
    }

    // Busca categorías por nombre.
    @FXML
    private void handleBuscar() {
        String nombre = txtBuscar.getText().trim();
        if (nombre.isEmpty()) {
            gestorPantallas.mostrarAlerta("Atención", "Ingrese un nombre para buscar.");
            cargarListaAsync();
            return;
        }

        Task<Categoria> task = new Task<>() {
            @Override
            protected Categoria call() {
                return servicioCa.buscarPorNombre(nombre);
            }
        };
        task.setOnSucceeded(evt -> {
            Categoria c = task.getValue();
            listaObservable.clear();
            if (c != null) listaObservable.add(c);
        });
        new Thread(task).start();
    }

    // Recarga la lista de categorías.
    @FXML
    private void handleListar() { cargarListaAsync(); }

    // Limpia los campos de texto y la selección de la tabla.
    @FXML
    private void handleLimpiar() { limpiarCampos();}

    // Vuelve a la pantalla del reclutador.
    @FXML
    private void onVolver(ActionEvent event) { gestorPantallas.irReclutador(); }

    // Carga la lista de categorías de manera asíncrona.
    private void cargarListaAsync() {
        Task<List<Categoria>> task = new Task<>() {
            @Override
            protected List<Categoria> call() {
                return servicioCa.listarCategorias();
            }
        };
        task.setOnSucceeded(evt -> listaObservable.setAll(task.getValue()));
        new Thread(task).start();
    }

    // Limpia los campos de texto y la selección de la tabla.
    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtBuscar.clear();
        tablaCategorias.getSelectionModel().clearSelection();
    }
}
