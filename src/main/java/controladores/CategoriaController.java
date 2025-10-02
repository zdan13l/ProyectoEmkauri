package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Categoria;
import repositorio.RCategoria;

import java.util.List;

public class CategoriaController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField descripcionField;
    @FXML
    private TableView<Categoria> categoriasTable;
    @FXML
    private TableColumn<Categoria, Integer> idColumn;
    @FXML
    private TableColumn<Categoria, String> nombreColumn;
    @FXML
    private TableColumn<Categoria, String> descripcionColumn;

    private RCategoria rCategoria;
    private ObservableList<Categoria> categoriaList;

    @FXML
    public void initialize() {
        rCategoria = new RCategoria();
        categoriaList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        cargarCategorias();

        categoriasTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nombreField.setText(newSelection.getNombre());
                descripcionField.setText(newSelection.getDescripcion());
            }
        });
    }

    private void cargarCategorias() {
        List<Categoria> categorias = rCategoria.findAll();
        categoriaList.setAll(categorias);
        categoriasTable.setItems(categoriaList);
    }

    @FXML
    public void onCreateClick(ActionEvent event) {
        String nombre = nombreField.getText();
        String descripcion = descripcionField.getText();

        if (nombre.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "El nombre d e la categoría no puede estar vacío.");
            return;
        }

        Categoria nuevaCategoria = new Categoria(0, nombre, descripcion);
        Categoria categoriaGuardada = rCategoria.save(nuevaCategoria);

        if (categoriaGuardada != null) {
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Categoría creada correctamente.");
            cargarCategorias();
            limpiarCampos();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear la categoría.");
        }
    }

    @FXML
    public void onEditClick(ActionEvent event) {
        Categoria categoriaSeleccionada = categoriasTable.getSelectionModel().getSelectedItem();

        if (categoriaSeleccionada == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selecciona una categoría para editar.");
            return;
        }

        String nuevoNombre = nombreField.getText();
        String nuevaDescripcion = descripcionField.getText();

        if (nuevoNombre.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "El nombre de la categoría no puede estar vacío.");
            return;
        }

        categoriaSeleccionada.setNombre(nuevoNombre);
        categoriaSeleccionada.setDescripcion(nuevaDescripcion);
        rCategoria.update(categoriaSeleccionada);

        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Categoría actualizada correctamente.");
        cargarCategorias();
        limpiarCampos();
    }

    @FXML
    public void onDeleteClick(ActionEvent event) {
        Categoria categoriaSeleccionada = categoriasTable.getSelectionModel().getSelectedItem();

        if (categoriaSeleccionada == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Selecciona una categoría para eliminar.");
            return;
        }

        rCategoria.delete(categoriaSeleccionada.getIdCategoria());
        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Categoría eliminada correctamente.");
        cargarCategorias();
        limpiarCampos();
    }

    private void limpiarCampos() {
        nombreField.clear();
        descripcionField.clear();
        categoriasTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
