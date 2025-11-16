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

// Controlador para manejar la pantalla del Emprendedor.
public class EmprendedorController {

    // Campos vinculados a los elementos de la interfaz.
    @FXML private Label lblBienvenidaTop;
    @FXML private Button btnCalificaciones;
    @FXML private Button btnIrCrearCurso;
    @FXML private Button btnIrCrearServicio;
    @FXML private Button btnMisProductos;
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
    public EmprendedorController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
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

    // Inicializa la pantalla mostrando el nombre del usuario.
    @FXML
    public void initialize() {
        Usuario usuario = SesionActual.getUsuarioActual();
        if (usuario != null && usuario.getDatosPersonales() != null) {
            lblBienvenidaTop.setText("¡Bienvenido, " + usuario.getDatosPersonales().getNombre() + "!");
        } else {
            lblBienvenidaTop.setText("¡Bienvenido!");
        }
    }

    // Ver mis productos.
    @FXML
    public void onVerMisProductos(ActionEvent event) {
        gestorPantallas.irProductosEmprendedor();
    }

    // Crear un nuevo curso.
    @FXML
    public void onCrearCurso(ActionEvent event) {
        abrirSolicitud("curso");
    }

    // Crear un nuevo servicio.
    @FXML
    public void onCrearServicio(ActionEvent event) {
        abrirSolicitud("servicio");
    }

    // Abre el formulario de solicitud de producto según el tipo.
    private void abrirSolicitud(String tipo) {
        gestorPantallas.irSolicitudProducto();
    }

    // Ver calificaciones.
    @FXML
    public void onVerCalificaciones(ActionEvent event) {
        gestorPantallas.irCalificaciones();
    }

    // Cerrar sesión y volver a la pantalla de login.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        gestorPantallas.irLogin();
    }
}
