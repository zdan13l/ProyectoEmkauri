package controladores;

import fis.jave.emkauri.SesionActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.Pago;
import modelo.Producto;
import servicio.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

// Controlador para manejar el proceso de pago y comprobante.
public class PagoController {

    // Servicios para manejar la lógica de negocio.
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;
    private final ISSolicitud servicioS;

    // Constructor con inyección de dependencias.
    public PagoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa, ISSolicitud servicioS) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
        this.servicioS = servicioS;
    }

    // Campos de la pantalla de pago.
    @FXML private TextField txtTitular;
    @FXML private TextField txtNumeroTarjeta;
    @FXML private TextField txtMes;
    @FXML private TextField txtAnio;
    @FXML private PasswordField txtCVV;
    @FXML private TextField txtCorreo;
    @FXML private Label lblTotalPago;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmarPago;

    // Campos de la pantalla de comprobante.
    @FXML private Label lblNombreCliente;
    @FXML private Label lblCorreoCliente;
    @FXML private Label lblTotalPagado;
    @FXML private Label lblFecha;
    @FXML private Label lblCodigoTransaccion;
    @FXML private Button btnVolverInicio;
    @FXML private Button btnGuardar;

    private Pago pagoActual;

    // Inicialización de la pantalla.
    @FXML
    public void initialize() {
        if (lblTotalPago != null) { // pantalla de pago
            double total = SesionActual.getCarrito().stream()
                    .mapToDouble(Producto::getPrecio)
                    .sum();
            lblTotalPago.setText(String.format("$ %.2f", total));
        } else if (lblFecha != null) { // pantalla de comprobante
            inicializarComprobante();
        }
    }

    // Maneja la acción de cancelar el pago.
    @FXML
    public void handleCancelar(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/carrito.fxml", "Carrito de Compras", btnCancelar);
    }

    // Maneja la acción de confirmar el pago.
    @FXML
    public void handleConfirmarPago(ActionEvent event) {
        if (txtTitular == null) return;

        if (txtTitular.getText().isEmpty() || txtNumeroTarjeta.getText().isEmpty()
                || txtMes.getText().isEmpty() || txtAnio.getText().isEmpty()
                || txtCVV.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor completa todos los datos del pago.");
            return;
        }

        double total = SesionActual.getCarrito().stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        pagoActual = new Pago();
        pagoActual.setCodigo(UUID.randomUUID().toString().substring(0, 8));
        pagoActual.setMonto(total);
        pagoActual.setFecha(new Date());

        SesionActual.setPagoActual(pagoActual);

        cambiarPantalla("/puj.fis.pantallas/comprobante.fxml", "Pago Exitoso", btnConfirmarPago);
    }

    // Inicializa los datos del comprobante.
    private void inicializarComprobante() {
        pagoActual = SesionActual.getPagoActual();

        if (SesionActual.getUsuarioActual() != null) {
            lblNombreCliente.setText("Nombre: " + SesionActual.getUsuarioActual().getDatosPersonales().getNombre());
            lblCorreoCliente.setText("Correo: " + SesionActual.getUsuarioActual().getCorreo());
        }

        double total = SesionActual.getCarrito().stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        lblTotalPagado.setText("Total Pagado: $" + String.format("%.2f", total));
        lblFecha.setText("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblCodigoTransaccion.setText("Código de transacción: #" + UUID.randomUUID().toString().substring(0, 8));
    }

    // Maneja la acción de guardar el comprobante en un archivo JSON.
    @FXML
    public void handleGuardar(ActionEvent event) {
        if (pagoActual == null) {
            mostrarAlerta("Error", "No hay un pago registrado para guardar.");
            return;
        }

        String json = "{"
                + "\"codigo\":\"" + escape(pagoActual.getCodigo()) + "\","
                + "\"monto\":" + pagoActual.getMonto() + ","
                + "\"fecha\":\"" + escape(pagoActual.getFecha().toString()) + "\""
                + "}";

        try {
            Path carpeta = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "comprobantes");
            Files.createDirectories(carpeta);

            Path destino = carpeta.resolve("comprobante_" + pagoActual.getCodigo() + ".json");
            Files.writeString(destino, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            mostrarAlerta("Guardado exitoso", "Comprobante guardado correctamente en:\n" + destino.toAbsolutePath());

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar el comprobante:\n" + e.getMessage());
        }
    }

    // Maneja la acción de volver al inicio.
    @FXML
    public void handleVolverInicio(ActionEvent event) {
        cambiarPantalla("/puj.fis.pantallas/cliente.fxml", "Menú Principal", btnVolverInicio);
        SesionActual.vaciarCarrito();
    }

    // Muestra una alerta con el título y mensaje proporcionados.
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Escapa caracteres especiales en una cadena para JSON.
    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Cambia la pantalla actual a la especificada por el path FXML.
    private void cambiarPantalla(String fxmlPath, String titulo, Button boton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa, servicioS);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(scene);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla: " + titulo);
        }
    }
}
