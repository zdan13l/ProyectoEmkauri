package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import modelos.Calificacion;

public class CalificacionController {

    @FXML
    private TableView<Calificacion> calificacionesTable;

    @FXML
    private TableColumn<Calificacion, String> cursoColumn;

    @FXML
    private TableColumn<Calificacion, Double> notaColumn;

    @FXML
    private TableColumn<Calificacion, String> comentarioColumn;

    @FXML
    private TableColumn<Calificacion, String> fechaColumn;

    /**
     * Método que se ejecuta al inicializar la vista.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        cargarCalificacionesDemo();
    }

    /**
     * Configura las columnas de la tabla para vincularlas con la clase modelo.
     */
    private void configurarColumnas() {
        cursoColumn.setCellValueFactory(new PropertyValueFactory<>("curso"));
        notaColumn.setCellValueFactory(new PropertyValueFactory<>("nota"));
        comentarioColumn.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }

    /**
     * Carga calificaciones de ejemplo (puedes cambiarlo por datos desde BD).
     */
    private void cargarCalificacionesDemo() {
        ObservableList<Calificacion> lista = FXCollections.observableArrayList(
                new Calificacion("Matemáticas", 4.5, "Buen desempeño", "2025-03-10"),
                new Calificacion("Programación", 4.8, "Excelente trabajo", "2025-04-15"),
                new Calificacion("Bases de Datos", 4.2, "Entrega a tiempo", "2025-05-05"),
                new Calificacion("Ética Profesional", 5.0, "Participación destacada", "2025-06-01")
        );

        calificacionesTable.setItems(lista);
    }
}

