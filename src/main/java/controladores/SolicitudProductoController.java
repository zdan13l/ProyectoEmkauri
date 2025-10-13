package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class SolicitudProductoController {

    @FXML
    private Label lblTitulo;

    @FXML
    private VBox contenedorSolicitudes;

    @FXML
    private Button btnVolver;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    public void initialize() {
        System.out.println("[INFO] Pantalla de solicitudes de producto inicializada.");
    }

    @FXML
    private void onVolver(ActionEvent event) {
        System.out.println("[ACCION] Volver al menú del reclutador.");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        System.out.println("[ACCION] Cerrar sesión desde solicitudes de producto.");

    }
}

