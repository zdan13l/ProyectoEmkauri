package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SolicitudProductoController {


    @FXML
    private Button btnVolver;
    @FXML
    private Button btnAprobar;
    @FXML
    private Button btnRechazar;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private VBox contenedorSolicitudes;

    @FXML
    public void onVolver(ActionEvent actionEvent) {
    }

    @Deprecated
    public void onEnviarSolicitud(ActionEvent actionEvent) {
    }

    @FXML
    public void onCerrarSesion(ActionEvent actionEvent) {
    }
}
