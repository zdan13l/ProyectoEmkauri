package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import modelo.Producto;
import modelo.Solicitud;
import modelo.Usuario;
import servicio.*;
import java.io.IOException;
import java.util.List;

// Controlador para la pantalla de gestión de solicitudes (productos y emprendedores).
public class SolicitudController {

    // Servicios inyectados.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;
    private final ISCalificacion servicioCal;
    private final GestorPantallas gestorPantallas;

    // Tipo de solicitud a mostrar: "producto" o "emprendedor".
    private String tipoSolicitud;

    // Elementos FXML.
    @FXML private VBox contenedorSolicitudes;
    @FXML private Button btnVolver;
    @FXML private Button btnCerrarSesion;

    // Constructor con inyección de dependencias.
    public SolicitudController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa,
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

    // Setter para definir el tipo de solicitud.
    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    // Inicialización del controlador.
    @FXML
    public void initialize() {
        System.out.println("[INFO] Pantalla de solicitudes inicializada.");
    }

    // Cargar solicitudes pendientes desde el servicio.
    public void cargarSolicitudesPendientes() {
        contenedorSolicitudes.getChildren().clear();

        try {
            List<Solicitud> solicitudes;

            if ("producto".equalsIgnoreCase(tipoSolicitud)) {
                solicitudes = servicioS.listarSolicitudesPendientes("PRODUCTO");
            } else if ("emprendedor".equalsIgnoreCase(tipoSolicitud)) {
                solicitudes = servicioS.listarSolicitudesPendientes("EMPRENDEDOR");
            } else {
                throw new IllegalArgumentException("Tipo de solicitud inválido: " + tipoSolicitud);
            }

            if (solicitudes.isEmpty()) {
                Label vacio = new Label("No hay solicitudes pendientes por revisar.");
                vacio.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
                contenedorSolicitudes.getChildren().add(vacio);
                return;
            }

            for (Solicitud solicitud : solicitudes) {
                contenedorSolicitudes.getChildren().add(crearCardSolicitud(solicitud));
            }

        } catch (Exception e) {
            Label error = new Label("Error al cargar las solicitudes: " + e.getMessage());
            contenedorSolicitudes.getChildren().add(error);
        }
    }

    // Crear un card visual para cada solicitud.
    private VBox crearCardSolicitud(Solicitud solicitud) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Información básica de la solicitud.
        Label lblTitulo = new Label("📋 Solicitud #" + solicitud.getIdSolicitud());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        Label lblEstado = new Label("Estado: " + solicitud.getEstado());
        Label lblMensaje = new Label("Mensaje: " + solicitud.getMensaje());
        lblMensaje.setWrapText(true);

        // Si la solicitud es de producto.
        if ("producto".equalsIgnoreCase(tipoSolicitud) && solicitud.getProductoAsociado() != null) {
            Producto p = solicitud.getProductoAsociado();
            Label lblNombre = new Label("Producto: " + p.getTitulo());
            Label lblCategoria = new Label("Categoría: " + (p.getCategoria() != null ? p.getCategoria().getNombre() : "Desconocida"));
            Label lblPrecio = new Label("Precio: $" + p.getPrecio());
            card.getChildren().addAll(lblNombre, lblCategoria, lblPrecio);
        }

        // Si la solicitud es de emprendedor.
        if ("emprendedor".equalsIgnoreCase(tipoSolicitud) && solicitud.getEmprendedor() != null) {
            Usuario u = solicitud.getEmprendedor();
            Label lblNombre = new Label("Nombre: " + u.getDatosPersonales().getNombre() + " " + u.getDatosPersonales().getApellido());
            Label lblCorreo = new Label("Correo: " + u.getCorreo());
            card.getChildren().addAll(lblNombre, lblCorreo);
        }

        // Botones de acción.
        HBox acciones = new HBox(10);
        acciones.setStyle("-fx-alignment: center-right;");

        Button btnAprobar = new Button("Aprobar");
        btnAprobar.setStyle("-fx-background-color: #16A34A; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAprobar.setOnAction(e -> aprobarSolicitud(solicitud.getIdSolicitud()));

        Button btnRechazar = new Button("Rechazar");
        btnRechazar.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRechazar.setOnAction(e -> rechazarSolicitud(solicitud.getIdSolicitud()));

        acciones.getChildren().addAll(btnAprobar, btnRechazar);

        // Ensamblar card final.
        card.getChildren().addAll(lblTitulo, lblEstado, lblMensaje, acciones);

        return card;
    }

    // Aprobar solicitud.
    private void aprobarSolicitud(int idSolicitud) {
        try {
            boolean exito = servicioS.aprobarSolicitud(idSolicitud);
            if (exito) {
                gestorPantallas.mostrarAlerta("Solicitud aprobada", "La solicitud fue aprobada correctamente.");
                cargarSolicitudesPendientes();
            } else {
                gestorPantallas.mostrarAlerta("No se pudo aprobar", "No se encontró la solicitud o ya fue procesada.");
            }
        } catch (Exception e) {
            gestorPantallas.mostrarAlerta("Error", "No se pudo aprobar la solicitud: " + e.getMessage());
        }
    }

    // Rechazar solicitud
    private void rechazarSolicitud(int idSolicitud) {
        try {
            boolean exito = servicioS.rechazarSolicitud(idSolicitud);
            if (exito) {
                gestorPantallas.mostrarAlerta(" Solicitud rechazada", "La solicitud fue rechazada correctamente.");
                cargarSolicitudesPendientes();
            } else {
                gestorPantallas.mostrarAlerta(" No se pudo rechazar", "No se encontró la solicitud o ya fue procesada.");
            }
        } catch (Exception e) {
            gestorPantallas.mostrarAlerta("Error", "No se pudo rechazar la solicitud: " + e.getMessage());
        }
    }

    // Volver al panel del reclutador.
    @FXML
    private void onVolver(ActionEvent event) {
        gestorPantallas.irReclutador();
    }

    // Cerrar sesión.
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        gestorPantallas.irLogin();
    }
}
