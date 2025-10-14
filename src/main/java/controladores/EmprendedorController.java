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

public class EmprendedorController {

    @FXML private Label lblBienvenidaTop;
    @FXML private Button btnCalificaciones;
    @FXML private Button btnIrCrearCurso;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnIrCrearServicio;
    @FXML private Button btnMisProductos;
    @FXML private ImageView welcomeIllustration;

    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    // Constructor con todos los servicios
    public EmprendedorController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    @FXML
    public void initialize() {
        Usuario usuario = SesionActual.getUsuarioActual();
        if (usuario != null && usuario.getDatosPersonales() != null) {
            lblBienvenidaTop.setText("¡Bienvenido, " + usuario.getDatosPersonales().getNombre() + "!");
        } else {
            lblBienvenidaTop.setText("¡Bienvenido!");
        }
    }

    // 🔹 Ver mis productos
    @FXML
    public void onVerMisProductos(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/misProductos.fxml", "Mis Productos");
    }

    @FXML
    public void onCrearCurso(ActionEvent event) {
        abrirSolicitud("curso");
    }

    @FXML
    public void onCrearServicio(ActionEvent event) {
        abrirSolicitud("servicio");
    }

    // 🔹 Nuevo método auxiliar para abrir el formulario de solicitud con tipo predeterminado
    private void abrirSolicitud(String tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/solicitudProducto.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            SolicitudProductoController controller = loader.getController();

            // 🔹 Primero, pasar el tipo
            controller.setTipoPredeterminado(tipo);

            // 🔹 Luego, configurar la selección visual (ya con la vista cargada)
            controller.configurarTipo();

            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Crear Nuevo " + (tipo.equals("curso") ? "Curso" : "Servicio"));
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el formulario de solicitud.");
            e.printStackTrace();
        }
    }



    // 🔹 Ver calificaciones
    @FXML
    public void onVerCalificaciones(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/calificaciones.fxml", "Mis Calificaciones");
    }

    // 🔹 Cerrar sesión
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        try {
            SesionActual.cerrarSesion();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inicio de Sesión");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cerrar sesión correctamente.");
            e.printStackTrace();
        }
    }

    // 🔹 Método auxiliar para cambiar de pantalla
    private void cambiarPantalla(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) lblBienvenidaTop.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla: " + titulo);
            e.printStackTrace();
        }
    }

    // 🔹 Mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
