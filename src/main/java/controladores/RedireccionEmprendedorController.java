package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class RedireccionEmprendedorController {

    @FXML
    private Label lblBienvenida;

    @FXML
    private VBox contenedorElementos;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnCrearServicio;

    @FXML
    private Button btnCrearCurso;

    @FXML
    private Button btnAdminCalificaciones;

    @FXML
    private Button btnMisServicios;

    @FXML
    private Button btnMisCursos;

    @FXML
    public void initialize() {
        lblBienvenida.setText("¡Bienvenido, Emprendedor!");
        System.out.println("[INFO] Pantalla principal del emprendedor inicializada correctamente.");
    }

    // --- ACCIONES DE LOS BOTONES ---

    @FXML
    private void onCrearServicio(ActionEvent event) {
        System.out.println("[ACCION] Crear nuevo servicio.");
        // Aquí puedes redirigir a la vista de creación de servicios
        // Ejemplo:
        // App.setRoot("CrearServicio");
    }

    @FXML
    private void onCrearCurso(ActionEvent event) {
        System.out.println("[ACCION] Crear nuevo curso.");
        // App.setRoot("CrearCurso");
    }

    @FXML
    private void onAdminCalificaciones(ActionEvent event) {
        System.out.println("[ACCION] Administrar calificaciones.");
        // App.setRoot("AdministrarCalificaciones");
    }

    @FXML
    private void onMisServicios(ActionEvent event) {
        System.out.println("[ACCION] Ver mis servicios.");
        // App.setRoot("MisServicios");
    }

    @FXML
    private void onMisCursos(ActionEvent event) {
        System.out.println("[ACCION] Ver mis cursos.");
        // App.setRoot("MisCursos");
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        System.out.println("[ACCION] Cerrar sesión del emprendedor.");
        // App.setRoot("login");
    }
}

