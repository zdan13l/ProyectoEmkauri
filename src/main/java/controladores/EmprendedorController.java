package controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import modelo.Usuario;
import java.io.IOException;

public class EmprendedorController {

    @FXML private Label lblBienvenidaTop;

    private Usuario emprendedor;

    // Inyectamos el emprendedor actual (desde el login)
    public void setEmprendedor(Usuario usuario) {
        this.emprendedor = usuario;
        lblBienvenidaTop.setText("¡Bienvenido, " + usuario.getDatosPersonales().getNombre() + "!");
    }

    // 🔹 Cerrar sesión
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inicio de Sesión");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Ir a “Mis Productos”
    @FXML
    private void onVerMisProductos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/misProductos.fxml"));
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Mis Productos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Crear Curso
    @FXML
    private void onCrearCurso(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/solicitudProducto.fxml"));
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Crear Curso");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Crear Servicio
    @FXML
    private void onCrearServicio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/solicitudProducto.fxml"));
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Crear Servicio");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Administrar calificaciones
    @FXML
    private void onVerCalificaciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/calificaciones.fxml"));
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Administrar Calificaciones");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
