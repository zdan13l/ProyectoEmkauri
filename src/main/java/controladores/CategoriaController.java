package controladores;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
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

/**
 * Controlador de la pantalla de gestión de Categorías.
 * Maneja CRUD completo e inyección de dependencias.
 */
public class CategoriaController {

    // ----------------------------------------------------
    // 🔹 Dependencias inyectadas
    // ----------------------------------------------------
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    public CategoriaController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP,
                               ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    // ----------------------------------------------------
    // 🔹 Componentes FXML
    // ----------------------------------------------------
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colDescripcion;
    @FXML private Button btnVolver;

    private final ObservableList<Categoria> listaObservable = FXCollections.observableArrayList();

    // ----------------------------------------------------
    // 🔹 Inicialización
    // ----------------------------------------------------
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

        BooleanBinding sinSeleccion = tablaCategorias.getSelectionModel().selectedItemProperty().isNull();
        btnVolver.disableProperty().set(false); // siempre activo
    }

    // ----------------------------------------------------
    // 🔹 CRUD
    // ----------------------------------------------------
    @FXML
    private void handleAgregar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", null, "El nombre no puede estar vacío.");
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
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", null, "Categoría creada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo crear la categoría.");
            }
        });

        task.setOnFailed(evt -> mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                "Error al crear la categoría: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void handleActualizar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", null, "Seleccione una categoría para actualizar.");
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevaDescripcion = txtDescripcion.getText().trim();

        if (nuevoNombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", null, "El nombre no puede estar vacío.");
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
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", null, "Categoría actualizada correctamente.");
                limpiarCampos();
                cargarListaAsync();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo actualizar la categoría.");
            }
        });

        task.setOnFailed(evt -> mostrarAlerta(Alert.AlertType.ERROR, "Error", null,
                "Error al actualizar: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void handleEliminar() {
        Categoria seleccion = tablaCategorias.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", null, "Seleccione una categoría para eliminar.");
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
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", null, "Categoría eliminada correctamente.");
                    limpiarCampos();
                    cargarListaAsync();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo eliminar la categoría.");
                }
            });

            new Thread(task).start();
        }
    }

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

    @FXML
    private void handleListar() { cargarListaAsync(); }

    @FXML
    private void handleLimpiar() { limpiarCampos(); }

    @FXML
    private void onVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/reclutador.fxml"));
            Controlador factory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(factory::createController);

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Panel del Reclutador");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo volver al panel del reclutador: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // 🔹 Utilidades
    // ----------------------------------------------------
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

    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtBuscar.clear();
        tablaCategorias.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String contenido) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(header);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        });
    }
}
