package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class SolicitudController {

    @FXML
    private VBox contenedorSolicitudes;

    @FXML
    private Button btnVolver;

    @FXML
    private Button btnCerrarSesion;
    @FXML
    private Button btnAprobar;
    @FXML
    private Button btnRechazar;

    @FXML
    public void initialize() {
        System.out.println("[INFO] Pantalla de solicitudes de emprendedor inicializada.");
    }

    @FXML
    private void onVolver(ActionEvent event) {
        System.out.println("[ACCION] Volver al menú del reclutador.");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        System.out.println("[ACCION] Cerrar sesión desde solicitudes de emprendedor.");
    }
}
