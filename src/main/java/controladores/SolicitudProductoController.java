package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SolicitudProductoController {


    @FXML
    private VBox boxCurso;
    @FXML
    private RadioButton rbCurso;
    @FXML
    private TextField txtCertificacion;
    @FXML
    private TextField txtDuracionServicio;
    @FXML
    private VBox boxServicio;
    @FXML
    private TextField txtUbicacion;
    @FXML
    private Button btnEnviarSolicitud;
    @FXML
    private ComboBox cmbCategoria;
    @FXML
    private TextField txtPrecio;
    @FXML
    private RadioButton rbServicio;
    @FXML
    private TextField txtModalidad;
    @FXML
    private Button btnVolver;
    @FXML
    private ToggleGroup tipoProductoGroup;
    @FXML
    private TextField txtDuracionCurso;
    @FXML
    private ScrollPane scrollForm;
    @FXML
    private Label lblConfirmacion;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtNivelDificultad;

    @FXML
    public void onVolver(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void onEnviarSolicitud(ActionEvent actionEvent) {
    }
}
