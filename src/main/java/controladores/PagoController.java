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

public class PagoController {

    // Servicios
    private final ISUsuario servicioU;
    private final ISCompra servicioCo;
    private final ISProducto servicioP;
    private final ISCategoria servicioCa;
    private final ISPago servicioPa;

    public PagoController(ISUsuario servicioU, ISCompra servicioCo, ISProducto servicioP, ISCategoria servicioCa, ISPago servicioPa) {
        this.servicioU = servicioU;
        this.servicioCo = servicioCo;
        this.servicioP = servicioP;
        this.servicioCa = servicioCa;
        this.servicioPa = servicioPa;
    }

    // ---- PANTALLA 1: FORMULARIO DE PAGO ----
    @FXML private TextField txtTitular;
    @FXML private TextField txtNumeroTarjeta;
    @FXML private TextField txtMes;
    @FXML private TextField txtAnio;
    @FXML private PasswordField txtCVV;
    @FXML private TextField txtCorreo;
    @FXML private Label lblTotalPago;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmarPago;

    // ---- PANTALLA 2: COMPROBANTE DE PAGO ----
    @FXML private Label lblNombreCliente;
    @FXML private Label lblCorreoCliente;
    @FXML private Label lblTotalPagado;
    @FXML private Label lblFecha;
    @FXML private Label lblCodigoTransaccion;
    @FXML private Button btnVolverInicio;
    @FXML private Button btnGuardar;

    private Pago pagoActual; // para guardar el comprobante
    // Inicialización automática (solo se ejecuta si los componentes existen)
    @FXML
    public void initialize() {
        if (lblTotalPago != null) {  // estamos en pantalla de pago
            double total = SesionActual.getCarrito().stream()
                    .mapToDouble(Producto::getPrecio)
                    .sum();
            lblTotalPago.setText(String.format("$ %.2f", total));
        } else if (lblFecha != null) {  // estamos en pantalla de comprobante
            inicializarComprobante();
        }
    }

    // Botón Cancelar en pantalla 1
    @FXML
    public void handleCancelar(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/carrito.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.setTitle("Carrito de Compras");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver al carrito.");
        }
    }

    // Botón Confirmar Pago en pantalla 1
    @FXML
    public void handleConfirmarPago(ActionEvent actionEvent) {
        if (txtTitular == null) return; // seguridad por si estamos en otra vista

        // Validaciones simples
        if (txtTitular.getText().isEmpty() || txtNumeroTarjeta.getText().isEmpty()
                || txtMes.getText().isEmpty() || txtAnio.getText().isEmpty()
                || txtCVV.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor completa todos los datos del pago.");
            return;
        }

        // Crear objeto Pago
        double total = SesionActual.getCarrito().stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        pagoActual = new Pago();
        pagoActual.setCodigo(UUID.randomUUID().toString().substring(0, 8));
        pagoActual.setMonto(total);
        pagoActual.setFecha(new Date());

        // Guardar en la sesión para que esté disponible en el comprobante
        SesionActual.setPagoActual(pagoActual);

        // Simular procesamiento y abrir pantalla de confirmación
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/comprobante.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnConfirmarPago.getScene().getWindow();
            stage.setTitle("Pago Exitoso");
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la pantalla de confirmación de pago.");
        }
    }

    // ---- Inicializar pantalla de comprobante ----
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

    // ---- Botón Guardar en pantalla de comprobante ----
    @FXML
    public void handleGuardar(ActionEvent actionEvent) {
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
            // 📂 Ruta de la carpeta comprobantes dentro del proyecto
            Path carpeta = Paths.get(System.getProperty("user.dir"),
                    "src", "main", "resources", "comprobantes");

            // Crear carpeta si no existe
            Files.createDirectories(carpeta);

            // Crear archivo con nombre único
            Path destino = carpeta.resolve("comprobante_" + pagoActual.getCodigo() + ".json");

            // Guardar el JSON
            Files.writeString(destino, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            mostrarAlerta("Guardado exitoso",
                    "Comprobante guardado correctamente en:\n" + destino.toAbsolutePath());

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar el comprobante:\n" + e.getMessage());
        }
    }


    // ---- Botón Volver al inicio en pantalla de comprobante ----
    @FXML
    public void handleVolverInicio(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puj.fis.pantallas/cliente.fxml"));
            Controlador controladorFactory = new Controlador(servicioU, servicioCo, servicioP, servicioCa, servicioPa);
            loader.setControllerFactory(controladorFactory::createController);

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnVolverInicio.getScene().getWindow();
            stage.setTitle("Menú Principal");
            stage.setScene(scene);

            // Vaciar carrito tras compra
            SesionActual.vaciarCarrito();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo volver al menú principal.");
        }
    }

    // ---- Utilidad ----
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Función mínima para escapar comillas y barras.
    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
