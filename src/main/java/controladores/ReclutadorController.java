package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelo.Usuario;
import servicio.*;
import java.io.IOException;

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
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public ReclutadorController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
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

    // Inicializa la pantalla con los datos del usuario actual.
    @FXML
    public void initialize() {
        Usuario reclutador = SesionActual.getUsuarioActual();

        if (reclutador != null) {
            lblBienvenidaTop.setText("Bienvenido, " +
                    reclutador.getDatosPersonales().getNombre() + " " +
                    reclutador.getDatosPersonales().getApellido());
        } else {
            lblBienvenidaTop.setText("Bienvenido, Reclutador");
        }
    }

    // Maneja el evento de clic en el botón de solicitudes de productos.
    @FXML
    private void onSolicitudesProducto(ActionEvent event) {
        gestorPantallas.irSolicitudProducto();
    }

    // Maneja el evento de clic en el botón de solicitudes de emprendedores.
    @FXML
    private void onSolicitudesEmprendedor(ActionEvent event) {
        gestorPantallas.irSolicitudEmprendedor();
    }

    // Abre la pantalla de solicitudes según el tipo especificado.
    private void abrirPantallaSolicitudes(String fxmlPath, String tipo, String titulo, Button boton) {
        // Lógica para abrir la pantalla de solicitudes. (PENDIENTE)
    }

    // Maneja el evento de clic en el botón de cerrar sesión.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        SesionActual.cerrarSesion();
        gestorPantallas.irLogin();
    }

    // Maneja el evento de clic en el botón de categorías.
    @FXML
    private void onCategorias(ActionEvent event) {
        gestorPantallas.irCategoria();
    }
}
