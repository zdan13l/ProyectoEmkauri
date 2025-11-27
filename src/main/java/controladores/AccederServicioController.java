package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import modelo.*;
import java.awt.Desktop;
import java.net.URI;
import java.util.List;

// Controlador para gestionar el acceso a un servicio comprado por el cliente.
public class AccederServicioController {

    // Elementos de la interfaz gráfica.
    @FXML private Label lblTituloServicio;
    @FXML private Label lblNombreEmprendedor;
    @FXML private Label lblTelefono;
    @FXML private Button btnWhatsapp;
    @FXML private Button btnVolver;

    // Servicios para la lógica y gestor de navegación.
    private final GestorPantallas gestorPantallas;

    // Servicio seleccionado.
    private Producto servicioSeleccionado;

    // Constructor que recibe los servicios necesarios.
    public AccederServicioController(GestorPantallas gestorPantallas) { this.gestorPantallas = gestorPantallas; }

    // Establece el servicio seleccionado y carga los datos correspondientes.
    public void setServicio(Producto servicio) {
        this.servicioSeleccionado = servicio;
        if (servicio != null) {
            lblTituloServicio.setText(servicio.getTitulo());
            cargarEmprendedor(servicio);
        }
    }

    // Inicializa la vista.
    @FXML
    private void initialize() {
        btnWhatsapp.setDisable(true);
    }

    // Maneja el evento de contactar por WhatsApp.
    @FXML
    private void onContactarWhatsapp(ActionEvent event) {
        String telefono = lblTelefono.getText();
        if (telefono == null || telefono.equals("—") || telefono.equals("No disponible")) { return; }

        String mensaje = "¡Hola! Me comunico desde Emkauri sobre el servicio que compré.";
        String url = "https://wa.me/" + telefono + "?text=" + mensaje.replace(" ", "%20");

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Maneja el evento de volver a la vista de productos del cliente.
    @FXML
    private void onVolver(ActionEvent event) { gestorPantallas.irProductosCliente(); }

    // Carga los datos del servicio seleccionado y los productos comprados.
    public void cargarDatos(Producto servicioSeleccionado, List<Producto> productosComprados) { setServicio(servicioSeleccionado); }

    // Carga la información del emprendedor asociado al servicio.
    private void cargarEmprendedor(Producto producto) {
        Usuario emprendedor = producto.getEmprendedor();
        if (emprendedor != null && emprendedor.getDatosPersonales() != null) {
            lblNombreEmprendedor.setText(emprendedor.getDatosPersonales().getNombre() + " " +
                                            emprendedor.getDatosPersonales().getApellido());

            String telefono = emprendedor.getDatosPersonales().getTelefono();
            lblTelefono.setText((telefono != null && !telefono.isBlank()) ? telefono : "No disponible");
            btnWhatsapp.setDisable(telefono == null || telefono.isBlank());
        } else {
            lblNombreEmprendedor.setText("No disponible");
            lblTelefono.setText("—");
            btnWhatsapp.setDisable(true);
        }
    }
}
