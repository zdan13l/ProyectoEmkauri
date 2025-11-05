package controladores;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.Categoria;
import servicio.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

// Controlador para manejar la pantalla de gestión de categorías (CRUD).
public class CategoriaController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colDescripcion;
    @FXML private Button btnVolver;

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;

    // Lista observable de categorías para la tabla.
    private final ObservableList<Categoria> listaObservable = FXCollections.observableArrayList();

    // Constructor que recibe los servicios necesarios.
    public CategoriaController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
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

        btnVolver.disableProperty().set(false); // siempre activo
    }

    // Agrega una nueva categoría.
    @FXML
    private void handleAgregar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre no puede estar vacío.");
            return;
        }

        Categoria nueva = new Categoria();
        nueva.setNombre(nombre);
        nueva.setDescripcion(descripcion.isEmpty() ? "Sin descripción" : descripcion);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return servicioCa.crearCategoria(nueva);
            }
        };

        task.setOnSucceeded(evt -> {
            if (task.getValue()) {
                mostrarAlerta("Éxito", "Categoría creada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                mostrarAlerta("Error", "No se pudo crear la categoría.");
            }
        });

        task.setOnFailed(evt -> mostrarAlerta("Error", "Error al crear la categoría: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // Actualiza la categoría seleccionada.
    @FXML
    private void handleActualizar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Atención", "Seleccione una categoría para actualizar.");
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevaDescripcion = txtDescripcion.getText().trim();

        if (nuevoNombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre no puede estar vacío.");
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
                mostrarAlerta("Éxito", "Categoría actualizada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la categoría.");
            }
        });

        task.setOnFailed(evt -> mostrarAlerta("Error", "Error al actualizar: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // Elimina la categoría seleccionada.
    @FXML
    private void handleEliminar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta("Atención", "Seleccione una categoría para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Eliminar la categoría '" + seleccion.getNombre() + "'?");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return servicioCa.eliminarCategoria(seleccion.getIdCategoria());
                }
            };

            task.setOnSucceeded(evt -> {
                if (task.getValue()) {
                    mostrarAlerta("Éxito", "Categoría eliminada correctamente.");
                    limpiarCampos();
                    cargarListaAsync();
                } else {
                    mostrarAlerta("Error", "No se pudo eliminar la categoría.");
                }
            });

            new Thread(task).start();
        }
    }

    // Busca categorías por nombre.
    @FXML
    private void handleBuscar() {
        String nombre = txtBuscar.getText().trim();
        if (nombre.isEmpty()) {
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
    private void handleLimpiar() { limpiarCampos(); }

    // Vuelve a la pantalla del reclutador.
    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/reclutador.fxml"));
            Controlador factory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS, servicioCal);
            loader.setControllerFactory(factory::createController);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Panel del Reclutador");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver al panel del reclutador: " + e.getMessage());
        }
    }

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

    // Muestra una alerta con título y mensaje proporcionados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
