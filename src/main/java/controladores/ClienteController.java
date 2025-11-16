package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import modelo.Usuario;
import servicio.ISUsuario;

// Controlador para gestionar la interfaz del cliente.
public class ClienteController {

    // Elementos de la interfaz gráfica.
    @FXML private Label lblBienvenidaTop;
    @FXML private Button btnCatalogo;
    @FXML private Button btnMisProductos;
    @FXML private Button btnCarrito;
    @FXML private Button btnCerrarSesion;
    @FXML private ImageView welcomeIllustration;

    // Elementos de la interfaz gráfica.
    private final ISUsuario servicioU;
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public ClienteController(ISUsuario servicioU, GestorPantallas gestorPantallas) {
        this.servicioU = servicioU;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicialización del controlador.
    @FXML
    public void initialize() {
        Usuario cliente = SesionActual.getUsuarioActual();
        String nombre = servicioU.obtenerNombre(cliente.getCorreo());
        String apellido = servicioU.obtenerApellido(cliente.getCorreo());
        lblBienvenidaTop.setText("¡Bienvenid@, Cliente " + nombre + " " +apellido + "!");
    }

    // Navegar al catálogo de productos.
    @FXML
    private void onVerCatalogo(ActionEvent event) {
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
