package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modelo.Categoria;
import repositorio.RCategoria;

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

    private RCategoria rCategoria;

    @FXML
    public void initialize() {

    }

    @FXML
    public void onSearchClick(ActionEvent event) {

    }
}
