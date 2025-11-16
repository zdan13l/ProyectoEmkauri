package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import modelo.Usuario;
import servicio.ISUsuario;

// Controlador para gestionar la interfaz del emprendedor.
public class EmprendedorController {

    // Elementos de la interfaz gráfica.
    @FXML private Label lblBienvenidaTop;
    @FXML private Button btnCalificaciones;
    @FXML private Button btnIrCrearCurso;
    @FXML private Button btnIrCrearServicio;
    @FXML private Button btnMisProductos;
    @FXML private Button btnCerrarSesion;
    @FXML private ImageView welcomeIllustration;

    // Elementos de la interfaz gráfica.
    private final ISUsuario servicioU;
    private final GestorPantallas gestorPantallas;

    // Constructor que recibe los servicios necesarios.
    public EmprendedorController(ISUsuario servicioU, GestorPantallas gestorPantallas) {
        this.servicioU = servicioU;
        this.gestorPantallas = gestorPantallas;
    }

    // Inicializa la pantalla mostrando el nombre del usuario.
    @FXML
    public void initialize() {
        Usuario emprendedor = SesionActual.getUsuarioActual();
        String nombre = servicioU.obtenerNombre(emprendedor.getCorreo());
        String apellido = servicioU.obtenerApellido(emprendedor.getCorreo());
        lblBienvenidaTop.setText("¡Bienvenid@, Emprendedor " + nombre + " " + apellido + "!");
    }

    // Ver mis productos.
    @FXML
    public void onVerMisProductos(ActionEvent event) { gestorPantallas.irProductosEmprendedor(); }

    // Crear un nuevo curso.
    @FXML
    public void onCrearCurso(ActionEvent event) { gestorPantallas.irSolicitudes("curso"); }

    // Crear un nuevo servicio.
    @FXML
    public void onCrearServicio(ActionEvent event) { gestorPantallas.irSolicitudes("servicio"); }

    // Ver calificaciones.
    @FXML
    public void onVerCalificaciones(ActionEvent event) { gestorPantallas.irCalificaciones(); }

    // Cerrar sesión y volver a la pantalla de login.
    @FXML
    private void onCerrarSesion(ActionEvent event) { gestorPantallas.irLogin(); }
}
