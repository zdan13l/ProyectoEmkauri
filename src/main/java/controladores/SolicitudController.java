package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import modelo.Solicitud;
import servicio.ISSolicitud;
import java.util.List;

// Controlador para gestionar la visualización y acciones sobre las solicitudes.
public class SolicitudController {

    // Elementos de la interfaz gráfica.
    @FXML private VBox contenedorSolicitudesPendientes;
    @FXML private VBox contenedorSolicitudesRevisadas;

    // Servicios para la lógica y gestor de navegación.
    private final ISSolicitud servicioS;
    private final GestorPantallas gestorPantallas;

    // Fábrica para crear tarjetas de solicitud y tipo de solicitud.
    private SolicitudCard cardFactory;
    private String tipoSolicitud;

    // Constructor que recibe los servicios necesarios.
    public SolicitudController(ISSolicitud servicioS, GestorPantallas gestorPantallas) {
        this.servicioS = servicioS;
        this.gestorPantallas = gestorPantallas;
    }

    // Setter para el tipo de solicitud y configuración de la fábrica de tarjetas.
    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
        this.cardFactory = new SolicitudCard(servicioS, this::cargarSolicitudes, tipoSolicitud);
    }

    // Volver al panel del reclutador.
    @FXML
    private void onVolver(ActionEvent e) { gestorPantallas.irReclutador(); }

    // Cerrar sesión y volver al login.
    @FXML
    private void onCerrarSesion(ActionEvent e) { gestorPantallas.irLogin(); }


    // Cargar y mostrar las solicitudes en los contenedores correspondientes.
    public void cargarSolicitudes() {
        contenedorSolicitudesPendientes.getChildren().clear();
        contenedorSolicitudesRevisadas.getChildren().clear();

        try {
            List<Solicitud> pendientes = servicioS.listarSolicitudesPendientes(tipoSolicitud);
            List<Solicitud> revisadas = servicioS.listarSolicitudes(tipoSolicitud);

            if (pendientes.isEmpty()) {
                contenedorSolicitudesPendientes.getChildren().add(new Label("No hay solicitudes pendientes."));
            } else {
                pendientes.forEach(solicitud ->
                        contenedorSolicitudesPendientes.getChildren()
                                .add(cardFactory.crearCard(solicitud, true)));
            }

            if (revisadas.isEmpty()) {
                contenedorSolicitudesRevisadas.getChildren().add(new Label("No hay solicitudes revisadas."));
            } else {
                revisadas.forEach(solicitud ->
                        contenedorSolicitudesRevisadas.getChildren()
                                .add(cardFactory.crearCard(solicitud, false)));
            }
        } catch (Exception ex) {
            gestorPantallas.mostrarAlerta("Error", ex.getMessage());
        }
    }
}
