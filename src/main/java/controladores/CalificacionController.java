package controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Calificacion;
import modelo.Cliente;
import modelo.Curso;
import repositorio.RCalificacion;

import java.util.Date;
import java.util.List;

public class CalificacionController {

    @FXML
    private TableView<Calificacion> calificacionesTable;
    @FXML
    private TableColumn<Calificacion, String> cursoColumn;
    @FXML
    private TableColumn<Calificacion, Integer> notaColumn;
    @FXML
    private TableColumn<Calificacion, String> comentarioColumn;
    @FXML
    private TableColumn<Calificacion, Date> fechaColumn;

    private Cliente clienteActual;
    private RCalificacion rCalificacion;
    private ObservableList<Calificacion> calificacionList;

    public void setCliente(Cliente cliente) {
        this.clienteActual = cliente;
        cargarCalificaciones();
    }

    @FXML
    public void initialize() {
        rCalificacion = new RCalificacion();
        calificacionList = FXCollections.observableArrayList();

        cursoColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCurso().getNombre()
        ));
        notaColumn.setCellValueFactory(new PropertyValueFactory<>("nota"));
        comentarioColumn.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }

    private void cargarCalificaciones() {
        if (clienteActual != null) {
            List<Calificacion> calificaciones = rCalificacion.findByClienteId(clienteActual.getIdUsuario());
            calificacionList.setAll(calificaciones);
            calificacionesTable.setItems(calificacionList);
        }
    }
}
