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

    // Constructor que recibe los servicios necesarios.
    public ReclutadorController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
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
        abrirPantallaSolicitudes("/puj.fis.pantallas/solicitudP.fxml", "producto", "Solicitudes de Productos", btnSolicitudesProducto);
    }

    // Maneja el evento de clic en el botón de solicitudes de emprendedores.
    @FXML
    private void onSolicitudesEmprendedor(ActionEvent event) {
        abrirPantallaSolicitudes("/puj.fis.pantallas/solicitudE.fxml", "emprendedor", "Solicitudes de Emprendedores", btnSolicitudesEmprendedor);
    }

    // Abre la pantalla de solicitudes según el tipo especificado.
    private void abrirPantallaSolicitudes(String fxmlPath, String tipo, String titulo, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Controlador factory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(factory::createController);

            Scene scene = new Scene(loader.load());
            SolicitudController controller = loader.getController();
            controller.setTipoSolicitud(tipo);
            controller.cargarSolicitudesPendientes();

            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("No se pudo abrir la pantalla de " + titulo.toLowerCase() + ".");
        }
    }

    // Maneja el evento de clic en el botón de cerrar sesión.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        SesionActual.cerrarSesion();
        cambiarPantalla("/puj.fis.pantallas/login.fxml", "Inicio de Sesión", btnCerrarSesion);
    }

    // Maneja el evento de clic en el botón de categorías.
    @FXML
    private void onCategorias(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/categoria.fxml", "Gestión Categorías", btnCategorias);
    }

    // Cambia a otra pantalla especificada por el path del FXML, título y botón que origina el cambio.
    private void cambiarPantalla(String fxmlPath, String titulo, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("No se pudo abrir la pantalla: " + titulo);
        }
    }

    // Muestra una alerta con el tipo, título, encabezado y mensaje especificados.
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
