package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class ReclutadorController {

    @FXML
    private Label lblBienvenida;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnSolicitudesProducto;

    @FXML
    private Button btnSolicitudesEmprendedor;

    @FXML
    public void initialize() {
        lblBienvenida.setText("¡Bienvenido, Reclutador!");
        System.out.println("[INFO] Pantalla principal del reclutador inicializada correctamente.");
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
