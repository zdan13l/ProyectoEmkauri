package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modelo.Categoria;
import modelo.Curso;
import modelo.Servicio;
import repositorio.RCategoria;
import repositorio.RCurso;
import repositorio.RServicio;

import java.util.ArrayList;
import java.util.List;

public class BusquedaController {

    @FXML
    private TextField busquedaField;
    @FXML
    private ComboBox<Categoria> categoriaFilter;
    @FXML
    private TableView<Object> resultadosTable;
    @FXML
    private TableColumn<Object, String> tipoColumn;
    @FXML
    private TableColumn<Object, String> nombreColumn;
    @FXML
    private TableColumn<Object, String> descripcionColumn;
    @FXML
    private TableColumn<Object, String> categoriaColumn;
    @FXML
    private TableColumn<Object, Double> precioColumn;

    private RCurso rCurso;
    private RServicio rServicio;
    private RCategoria rCategoria;

    @FXML
    public void initialize() {
        rCurso = new RCurso();
        rServicio = new RServicio();
        rCategoria = new RCategoria();

        tipoColumn.setCellValueFactory(cellData -> {
            Object item = cellData.getValue();
            if (item instanceof Curso) {
                return new javafx.beans.property.SimpleStringProperty("Curso");
            } else if (item instanceof Servicio) {
                return new javafx.beans.property.SimpleStringProperty("Servicio");
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        nombreColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descripcion"));
        categoriaColumn.setCellValueFactory(cellData -> {
            Object item = cellData.getValue();
            if (item instanceof Curso) {
                return new javafx.beans.property.SimpleStringProperty(((Curso) item).getCategoria().getNombre());
            } else if (item instanceof Servicio) {
                return new javafx.beans.property.SimpleStringProperty(((Servicio) item).getCategoria().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        precioColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("precio"));

        List<Categoria> categorias = rCategoria.findAll();
        categoriaFilter.getItems().add(new Categoria(0, "Todas", null));
        categoriaFilter.getItems().addAll(categorias);
        categoriaFilter.getSelectionModel().selectFirst(); // Seleccionar "Todas" por defecto
    }

    @FXML
    public void onSearchClick(ActionEvent event) {
        String query = busquedaField.getText();
        Categoria categoriaSeleccionada = categoriaFilter.getSelectionModel().getSelectedItem();
        int idCategoria = (categoriaSeleccionada != null) ? categoriaSeleccionada.getIdCategoria() : 0;

        List<Curso> cursos = rCurso.findByFiltros(query, idCategoria);
        List<Servicio> servicios = rServicio.findByFiltros(query, idCategoria);

        ObservableList<Object> resultados = FXCollections.observableArrayList();
        resultados.addAll(cursos);
        resultados.addAll(servicios);

        resultadosTable.setItems(resultados);
    }
}