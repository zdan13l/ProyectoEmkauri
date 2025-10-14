package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;

public class ReclutadorController {

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnSolicitudesProducto;

    @FXML
    private Button btnSolicitudesEmprendedor;
    @FXML
    private Button btnIrSolicitudesEmprendedor;
    @FXML
    private Button btnIrSolicitudesProducto;
    @FXML
    private Label lblBienvenidaTop;
    @FXML
    private ImageView logoEmkauri;

    @FXML
    public void initialize() {

    }

    @FXML
    private void onSolicitudesProducto(ActionEvent event) {
        System.out.println("[ACCION] Ver solicitudes de producto.");
    }

    @FXML
    private void onSolicitudesEmprendedor(ActionEvent event) {
        System.out.println("[ACCION] Ver solicitudes de emprendedor.");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        System.out.println("[ACCION] Cerrar sesión del reclutador.");
    }
}
