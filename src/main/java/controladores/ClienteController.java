package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import servicio.*;

import java.io.IOException;

// Controlador para manejar la lógica de la pantalla del cliente.
public class ClienteController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private Label lblBienvenidaTop;
    @FXML private Button btnCatalogo;
    @FXML private Button btnMisProductos;
    @FXML private Button btnCarrito;
    @FXML private Button btnCerrarSesion;
    @FXML private ImageView welcomeIllustration;

    // Servicio para manejar la lógica de usuario.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;

    public ClienteController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
    }

    // Ver catálogo de productos.
    @FXML
    private void onVerProductos(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/catalogo.fxml", "Catálogo de Productos");
    }

    // Ver mis productos (productos del cliente).
    @FXML
    private void onVerMisProductos(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/misProductos.fxml", "Mis Productos");
    }

    // Ver carrito de compras.
    @FXML
    private void onVerCarrito(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/carrito.fxml", "Mi Carrito");
    }

    // Cerrar sesión y volver a la pantalla de login.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            SesionActual.cerrarSesion();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setTitle("Login - Emkauri");
            stage.setScene(scene);

        } catch (IOException e) {
            mostrarAlerta("No se pudo cerrar sesión correctamente.");
        }
    }

    // Método genérico para cambiar de pantalla.
    private void cambiarPantalla(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Reutilizamos la fábrica de controladores, inyectando solo servicioUsuario.
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(scene);

        } catch (IOException e) {
            mostrarAlerta("No se pudo cargar la pantalla: " + fxmlPath);
        }
    }

    // Muestra una alerta con el título y mensaje proporcionados.
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
