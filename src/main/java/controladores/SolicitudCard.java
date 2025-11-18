package controladores;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import modelo.*;
import servicio.ISSolicitud;

// Clase para crear tarjetas de solicitud en la interfaz gráfica.
public class SolicitudCard {

    // Servicios y datos necesarios.
    private final ISSolicitud servicioS;
    private final Runnable refrescarPantalla;
    private final String tipoSolicitud;

    // Constructor que recibe los servicios y datos necesarios.
    public SolicitudCard(ISSolicitud servicioS, Runnable refrescarPantalla, String tipoSolicitud) {
        this.servicioS = servicioS;
        this.refrescarPantalla = refrescarPantalla;
        this.tipoSolicitud = tipoSolicitud;
    }

    // Crea una tarjeta HBox para una solicitud dada.
    public HBox crearCard(Solicitud solicitud, boolean esPendiente) {
        HBox card = new HBox(16);
        card.getStyleClass().add("card-box");

        VBox info = new VBox(6);

        Label titulo = new Label("📝 Solicitud #" + solicitud.getIdSolicitud());
        titulo.getStyleClass().add("card-title");
        info.getChildren().add(titulo);

        // Información del emprendedor.
        Usuario emprendedor = solicitud.getEmprendedor();
        if (emprendedor != null) {
            String nombre = safe(emprendedor.getDatosPersonales().getNombre()) + " " + safe(emprendedor.getDatosPersonales().getApellido());
            Label hechaPor = new Label("Hecha por: " + nombre);
            hechaPor.getStyleClass().add("card-text");
            info.getChildren().add(hechaPor);
        }

        // Información específica según el tipo de solicitud.
        if ("EMPRENDEDOR".equalsIgnoreCase(tipoSolicitud)) {
            agregarInfoEmprendedor(info, solicitud);
        } else if ("PRODUCTO".equalsIgnoreCase(tipoSolicitud)) {
            agregarInfoProducto(info, solicitud.getProductoAsociado());
        }

        // Estado y mensaje (común a todos).
        Label estado = new Label("Estado: " + safe(solicitud.getEstado()));
        estado.getStyleClass().add("card-text");
        info.getChildren().add(estado);

        // Mensaje de la solicitud.
        Label mensaje = new Label("💬 Mensaje: " + safe(solicitud.getMensaje()));
        mensaje.getStyleClass().add("card-text");
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(420);
        info.getChildren().add(mensaje);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox acciones = crearBotonesAccion(solicitud, esPendiente);
        card.getChildren().addAll(info, spacer, acciones);

        return card;
    }

    // Agrega información del emprendedor a la tarjeta.
    private void agregarInfoEmprendedor(VBox contenido, Solicitud solicitud) {
        Usuario usuario = solicitud.getEmprendedor();
        if (usuario != null) {
            contenido.getChildren().add(new Label("Teléfono: " + safe(usuario.getDatosPersonales().getTelefono())));
            contenido.getChildren().add(new Label("Correo: " + safe(usuario.getCorreo())));
        }
    }

    // Agrega información del producto a la tarjeta.
    private void agregarInfoProducto(VBox contenido, Producto producto) {
        if (producto == null) return;

        contenido.getChildren().add(new Label("Tipo: " + producto.getClass().getSimpleName()));
        contenido.getChildren().add(new Label("Nombre: " + safe(producto.getTitulo())));

        String categoria = producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Desconocida";
        contenido.getChildren().add(new Label("Categoría: " + categoria));
        contenido.getChildren().add(new Label("Precio: $" + producto.getPrecio()));

        if (producto instanceof Curso curso) {
            contenido.getChildren().add(new Label("Duración: " + curso.getDuracionCurso() + " horas"));
        } else if (producto instanceof Servicio servicio) {
            contenido.getChildren().add(new Label("Duración: " + servicio.getDuracionServicio() + " minutos"));
        }
    }

    // Crea los botones de acción según el estado de la solicitud.
    private VBox crearBotonesAccion(Solicitud solicitud, boolean esPendiente) {
        VBox contenedor = new VBox(8);
        contenedor.setAlignment(Pos.CENTER_RIGHT);

        // Botones según el estado de la solicitud.
        if (esPendiente) {
            Button aprobado = new Button("Aprobar");
            aprobado.getStyleClass().add("btn-primary");
            aprobado.setOnAction(e -> {
                servicioS.aprobarSolicitud(solicitud.getIdSolicitud());
                refrescarPantalla.run();
            });

            Button rechazado = new Button("Rechazar");
            rechazado.getStyleClass().add("btn-danger");
            rechazado.setOnAction(e -> {
                servicioS.rechazarSolicitud(solicitud.getIdSolicitud());
                refrescarPantalla.run();
            });

            contenedor.getChildren().addAll(aprobado, rechazado);
        } else {
            Button modificar = new Button("Marcar como pendiente");
            modificar.getStyleClass().add("btn-secondary");
            modificar.setOnAction(e -> {
                servicioS.marcarPendiente(solicitud.getIdSolicitud());
                refrescarPantalla.run();
            });

            contenedor.getChildren().add(modificar);
        }

        // Devolver el contenedor de botones.
        return contenedor;
    }

    // Función auxiliar para manejar valores nulos.
    private String safe(String s) { return s == null ? "" : s; }
}
