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

// Controlador para manejar la pantalla del cliente.
public class ClienteController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private Label lblBienvenidaTop;
    @FXML private Button btnCatalogo;
    @FXML private Button btnMisProductos;
    @FXML private Button btnCarrito;
    @FXML private Button btnCerrarSesion;
    @FXML private ImageView welcomeIllustration;

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;

    // Constructor que recibe los servicios necesarios.
    public ClienteController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
    }

    // Navegar al catálogo de productos.
    @FXML
    private void onVerProductos(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/catalogo.fxml", "Catálogo de Productos");
    }

    // Navegar a la pantalla de "Mis Productos".
    @FXML
    private void onVerMisProductos(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/productosC.fxml", "Mis Productos");
    }

    // Navegar a la pantalla del carrito de compras.
    @FXML
    private void onVerCarrito(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/carrito.fxml", "Mi Carrito");
    }

    // Cierra sesión y vuelve a la pantalla de login.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            SesionActual.cerrarSesion();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS, servicioCal);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setTitle("Login - Emkauri");
            stage.setScene(scene);

        } catch (IOException e) {
            mostrarAlerta("No se pudo cerrar sesión correctamente.");
        }
    }

    // Metodo genérico para cambiar de pantalla.
    private void cambiarPantalla(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Reutilizamos la fábrica de controladores para inyectar los servicios.
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS, servicioCal);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(scene);

        } catch (IOException e) {
            mostrarAlerta("No se pudo cargar la pantalla: " + fxmlPath);
        }
    }

    // Muestra una alerta con el mensaje proporcionado.
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
