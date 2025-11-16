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

    // Gestor de pantallas para la navegación.
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public ClienteController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
                                ISPago servicioPa, ISSolicitud servicioS, ISCalificacion servicioCal, GestorPantallas gestorPantallas) {
        this.servicioCo = servicioCo;
        this.servicioU = servicioU;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
        this.servicioCal = servicioCal;
        this.gestorPantallas = gestorPantallas;
    }

    // Navegar al catálogo de productos.
    @FXML
    private void onVerProductos(ActionEvent event) {
        gestorPantallas.irCatalogo();
    }

    // Navegar a la pantalla de "Mis Productos".
    @FXML
    private void onVerMisProductos(ActionEvent event) {
        gestorPantallas.irProductosCliente();
    }

    // Navegar a la pantalla del carrito de compras.
    @FXML
    private void onVerCarrito(ActionEvent event) {
        gestorPantallas.irCarrito();
    }

    // Cierra sesión y vuelve a la pantalla de login.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        gestorPantallas.irLogin();
    }
}
