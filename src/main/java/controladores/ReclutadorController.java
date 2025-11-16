package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import modelo.Usuario;
import servicio.ISUsuario;

// Controlador para la pantalla principal del reclutador.
public class ReclutadorController {

    // Elementos de la interfaz gráfica.
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnSolicitudesProducto;
    @FXML private Button btnSolicitudesEmprendedor;
    @FXML private Button btnCategorias;
    @FXML private Label lblBienvenidaTop;
    @FXML private ImageView logoEmkauri;

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public ReclutadorController(ISUsuario servicioU, GestorPantallas gestorPantallas) {
        this.servicioU = servicioU;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicializa la pantalla con los datos del usuario actual.
    @FXML
    public void initialize() {
        Usuario reclutador = SesionActual.getUsuarioActual();
        String nombre = servicioU.obtenerNombre(reclutador.getCorreo());
        String apellido = servicioU.obtenerApellido(reclutador.getCorreo());
        lblBienvenidaTop.setText("Bienvenido, " + nombre + " " + apellido);
    }

    // Maneja el evento de clic en el botón de solicitudes de productos.
    @FXML
    private void onSolicitudesProducto(ActionEvent event) { gestorPantallas.irSolicitudProducto("producto"); }

    // Maneja el evento de clic en el botón de solicitudes de emprendedores.
    @FXML
    private void onSolicitudesEmprendedor(ActionEvent event) { gestorPantallas.irSolicitudEmprendedor("emprendedor"); }

    // Maneja el evento de clic en el botón de categorías.
    @FXML
    private void onCategorias(ActionEvent event) { gestorPantallas.irCategoria(); }

    // Maneja el evento de clic en el botón de cerrar sesión.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        SesionActual.cerrarSesion();
        gestorPantallas.irLogin();
    }
}
