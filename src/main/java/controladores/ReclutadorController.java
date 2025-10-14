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

public class ReclutadorController {

    @FXML private Button btnCerrarSesion;
    @FXML private Button btnSolicitudesProducto;
    @FXML private Button btnSolicitudesEmprendedor;
    @FXML private Button btnIrSolicitudesEmprendedor;
    @FXML private Button btnIrSolicitudesProducto;
    @FXML private Button btnCategorias;
    @FXML private Label lblBienvenidaTop;
    @FXML private ImageView logoEmkauri;

    // Servicios inyectados
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    public ReclutadorController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    // ==========================================================
    // 🔹 Inicialización
    // ==========================================================
    @FXML
    public void initialize() {
        Usuario reclutador = SesionActual.getUsuarioActual();

        if (reclutador != null) {
            lblBienvenidaTop.setText("Bienvenido, " + reclutador.getDatosPersonales().getNombre() + " " + reclutador.getDatosPersonales().getApellido());
        } else {
            lblBienvenidaTop.setText("Bienvenido, Reclutador");
        }
    }

    // ==========================================================
    // 🔹 Ver solicitudes de producto
    // ==========================================================
    @FXML
    private void onSolicitudesProducto(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/solicitudP.fxml"));
            Controlador factory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(factory::createController);

            Scene scene = new Scene(loader.load());
            SolicitudController controller = loader.getController();
            controller.setTipoSolicitud("producto");
            controller.cargarSolicitudesPendientes();

            Stage stage = (Stage) btnSolicitudesProducto.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Solicitudes de Productos");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla de solicitudes de productos.");
        }
    }


    // ==========================================================
    // 🔹 Ver solicitudes de emprendedor
    // ==========================================================
    @FXML
    private void onSolicitudesEmprendedor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/solicitudE.fxml"));
            Controlador factory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(factory::createController);

            Scene scene = new Scene(loader.load());
            SolicitudController controller = loader.getController();
            controller.setTipoSolicitud("emprendedor");
            controller.cargarSolicitudesPendientes();

            Stage stage = (Stage) btnSolicitudesEmprendedor.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Solicitudes de Emprendedores");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla de solicitudes de emprendedores.");
        }
    }


    // ==========================================================
    // 🔹 Cerrar sesión
    // ==========================================================
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        SesionActual.cerrarSesion();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/login.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inicio de Sesión");
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo regresar a la pantalla de login.");
        }
    }

    // ==========================================================
    // 🔹 Utilidad para mostrar alertas
    // ==========================================================
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onCategorias(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/categoria.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Stage stage = (Stage) btnCategorias.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Gestión Categorías");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla de agregar categorías.");
        }
    }
}
